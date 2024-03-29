package es.redmic.es.common.repository;

/*-
 * #%L
 * ElasticSearch
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.action.get.GetRequest;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequest.Item;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.BaseAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.es.common.queryFactory.QueryFactory;
import es.redmic.es.common.utils.ElasticSearchUtils;
import es.redmic.es.config.EsClientProvider;
import es.redmic.es.config.OrikaScanBeanESItfc;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.exception.custom.ResourceNotFoundException;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.exception.databinding.RequestNotValidException;
import es.redmic.exception.elasticsearch.ESCreateMappingException;
import es.redmic.exception.elasticsearch.ESNotExistsIndexException;
import es.redmic.exception.elasticsearch.ESQueryException;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.common.query.dto.SortDTO;
import es.redmic.models.es.common.query.dto.SuggestQueryDTO;

public abstract class RBaseESRepository<TModel extends BaseES<?>> implements IRBaseESRepository<TModel> {

	protected static final Logger LOGGER = LoggerFactory.getLogger(RBaseESRepository.class);

	private static final String UPDATE_NESTED_PROPERTY_SCRIPT_PATH = "/scripts/update-nested-property.txt";

	private static final String UPDATE_PROPERTY_SCRIPT_PATH = "/scripts/update-property.txt";

	@Value("${redmic.elasticsearch.check.mappings}")
	private boolean checkMappings;

	@Value("${redmic.elasticsearch.create.mappings}")
	private boolean createMappings;

	@Autowired
	protected EsClientProvider ESProvider;

	private QueryBuilder INTERNAL_QUERY = null;

	private String[] INDEX;
	private String TYPE;

	protected Boolean ROLLOVER_INDEX = false;

	protected Integer SUGGESTSIZE = 10;
	protected String PRETAGS = "<b>";
	protected String POSTTAGS = "</b>";

	protected Integer MAX_SIZE = 100000;

	protected String MAPPING_BASE_PATH = "/mappings/";
	protected String MAPPING_FILE_EXTENSION = ".json";

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected OrikaScanBeanESItfc orikaMapper;

	protected Class<TModel> typeOfTModel;

	protected RBaseESRepository() {
	}

	@SuppressWarnings("unchecked")
	protected RBaseESRepository(String[] index, String type) {
		this.INDEX = index;
		this.TYPE = type;

		if (getClass().getGenericSuperclass() instanceof ParameterizedType) {
			this.typeOfTModel = (Class<TModel>) ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
		}
	}

	@PostConstruct
	private void checkMappings() {
		if (checkMappings && (INDEX != null && TYPE != null)) {
			checkIndicesAndTypes();
		}
	}

	/**
	 * Chequea que los índices y tipos existan. En casos de no existir, si así es
	 * configurado, los manda a crear.
	 */
	private void checkIndicesAndTypes() {

		ClusterHealthResponse response;
		try {
			response = ESProvider.getClient().cluster().health(new ClusterHealthRequest(), RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}

		String[] indexes = getIndex();

		for (int i = 0; i < indexes.length; i++) {

			String index = indexes[i];

			if (response.getIndices() == null || response.getIndices().size() == 0
					|| response.getIndices().get(index) == null) {

				GetAliasesResponse aliasesResponse;
				try {
					aliasesResponse = ESProvider.getClient().indices().getAlias(new GetAliasesRequest(index),
							RequestOptions.DEFAULT);
				} catch (IOException e) {
					e.printStackTrace();
					throw new ESNotExistsIndexException(index);
				}

				boolean noExist = (aliasesResponse == null || aliasesResponse.getAliases() == null
						|| aliasesResponse.getAliases().size() == 0);

				if (noExist && createMappings) {
					prepareIndex(index);
				} else if (noExist && !checkMappings) {
					throw new ESNotExistsIndexException(index);
				}
			}

		}
	}

	/**
	 * En caso de no existir el index, se debe crear index y type con mapping. Si se
	 * trata de timeseries se debe crear en su lugar un template para aplicar a
	 * todos los futuros índices
	 */
	private void prepareIndex(String index) {

		if (Boolean.TRUE.equals(ROLLOVER_INDEX)) {
			createTemplate(index);
		} else {
			createIndex(index);
		}
	}

	private void createIndex(String index) {

		CreateIndexRequest request = new CreateIndexRequest(index);

		request.source(getSettings(index, getType()), XContentType.JSON);

		try {
			ESProvider.getClient().indices().create(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			throw new ESCreateMappingException(index);
		}
	}

	private void createTemplate(String index) {

		PutIndexTemplateRequest request = new PutIndexTemplateRequest(index + "-template");

		request.source(getSettings(index, TYPE), XContentType.JSON);

		try {
			ESProvider.getClient().indices().putTemplate(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			throw new ESCreateMappingException(index);
		}
	}

	/**
	 * Retorna las settings para cada uno de los type
	 */
	private String getSettings(String index, String type) {

		String mappingFilePath = getMappingFilePath(index, type);

		try {
			InputStream resource = new ClassPathResource(mappingFilePath).getInputStream();

			return IOUtils.toString(resource, Charset.forName(StandardCharsets.UTF_8.name()));
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("Error obteniendo mapping {}",  mappingFilePath);
			throw new ResourceNotFoundException(e);
		}
	}

	protected String getMappingFilePath(String index, String type) {
		return MAPPING_BASE_PATH + type + "/" + index + MAPPING_FILE_EXTENSION;
	}

	/**
	 * Función que comprueba si el resultado de una búsqueda es que el índice no
	 * existe
	 */
	protected boolean indexNoExistResponse(org.elasticsearch.action.search.MultiSearchResponse.Item[] responses) {
		return responses[0].isFailure() && responses[0].getFailure().getMessage().contains("no such index");
	}

	protected abstract JavaType getSourceType(Class<?> wrapperClass);

	protected GetResponse getRequest(String id) {
		return getRequest(id, null, null);
	}

	protected GetResponse getRequest(String id, String parentId) {
		return getRequest(id, parentId, null);
	}

	protected GetResponse getRequest(String id, String parentId, String grandparentId) {

		LOGGER.debug("FindById en {} {} con id {}", getIndex(), getType(), id);

		for (int i = 0; i < getIndex().length; i++) {

			GetRequest getRequest = new GetRequest(getIndex()[i], getType(), id.toString());

			if (parentId != null) {
				getRequest.parent(parentId);
			}
			if (grandparentId != null) {
				getRequest.routing(grandparentId);
			}

			LOGGER.debug("getRequest {} {} -> {}", getIndex(), getType(), getRequest.toString());

			GetResponse response;
			try {
				response = ESProvider.getClient().get(getRequest, RequestOptions.DEFAULT);
			} catch (IOException e) {
				e.printStackTrace();
				throw new ItemNotFoundException("id", id + " en el servicio " + getIndex()[i] + " - " + getType());
			}

			if (response != null && response.isExists()) {
				return response;
			}
		}

		throw new ItemNotFoundException("id", id + " en el servicio " + getType());
	}

	@Override
	public <TQueryDTO extends SimpleQueryDTO> List<String> suggest(TQueryDTO queryDTO) {

		return suggest(queryDTO, null);
	}

	@Override
	public <TQueryDTO extends SimpleQueryDTO> List<String> suggest(TQueryDTO queryDTO, QueryBuilder partialQuery) {

		SuggestQueryDTO suggestDTO = queryDTO.getSuggest();

		if (suggestDTO == null) {
			throw new RequestNotValidException("suggest", null);
		}
		String[] fields = suggestDTO.getSearchFields();
		Integer size = suggestDTO.getSize();
		String text = suggestDTO.getText();

		if (fields == null || fields.length == 0) {
			fields = getDefaultSuggestFields();
		}

		QueryBuilder termQuery = getTermQuery(queryDTO.getTerms());

		if (termQuery != null && partialQuery != null) {
			partialQuery = QueryBuilders.boolQuery().must(partialQuery).must(termQuery);
		} else if (termQuery != null) {
			partialQuery = QueryBuilders.boolQuery().must(termQuery);
		}

		LOGGER.debug("Suggest en {} {} con fields {} y texto {} y query interna {}",
			getIndex(), getType(), fields, text, partialQuery);

		SearchRequest searchRequest = new SearchRequest(INDEX);

		String[] suggestFields = ElasticSearchUtils.getSuggestFields(fields);

		BoolQueryBuilder queryBuilder = getQuery(queryDTO, getInternalQuery(), partialQuery);

		QueryBuilder query = QueryBuilders.multiMatchQuery(text, suggestFields)
				.type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX);

		if (queryBuilder != null) {
			query = QueryBuilders.boolQuery().must(query).must(queryBuilder);
		}

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query).size((size == null) ? SUGGESTSIZE : size)
			.highlighter(getHighlightBuilder(queryDTO.getSuggest().getSearchFields()));

		searchRequest.source(searchSourceBuilder);
		searchRequest.types(getType());

		SearchResponse searchResponse;

		LOGGER.debug("suggest {} {} -> {}", getIndex(), getType(), searchRequest.toString());

		try {
			searchResponse = ESProvider.getClient().search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESQueryException();
		}

		return ElasticSearchUtils.createHighlightResponse(searchResponse);
	}

	protected MultiGetResponse multigetRequest(MgetDTO dto) {

		return multigetRequest(dto, null);
	}

	protected MultiGetResponse multigetRequest(MgetDTO dto, String parentId) {

		return multigetRequest(dto, parentId, null);
	}

	protected MultiGetResponse multigetRequest(MgetDTO dto, String parentId, String grandParentId) {

		LOGGER.debug("Mget en {} {} con fields {} e ids {}", getIndex(), getType(), dto.getFields(), dto.getIds());

		MultiGetRequest request = new MultiGetRequest();

		FetchSourceContext fetchSourceContext;

		if (dto.getFields() == null || dto.getFields().isEmpty()) {
			fetchSourceContext = new FetchSourceContext(true);
		} else {
			String[] fieldsArray = dto.getFields().toArray(new String[dto.getFields().size()]);
			fetchSourceContext = new FetchSourceContext(true, fieldsArray, new String[0]);
		}

		int sizeIds = dto.getIds().size();
		for (int i = 0; i < getIndex().length; i++) {
			for (int k = 0; k < sizeIds; k++) {

				Item item = new Item(getIndex()[i], getType(), dto.getIds().get(k));

				if (parentId != null) {
					item.parent(parentId);
				}
				if (grandParentId != null) {
					item.routing(grandParentId);
				}
				item.fetchSourceContext(fetchSourceContext);
				request.add(item);
			}
		}

		LOGGER.debug("multigetRequest {} {} -> {}", getIndex(), getType(), request.toString());

		try {
			return ESProvider.getClient().mget(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			throw new ESQueryException();
		}
	}

	protected SearchResponse searchRequest(QueryBuilder query) {

		return searchRequest(query, SortBuilders.fieldSort("id").order(SortOrder.ASC), null);
	}

	protected SearchResponse searchRequest(QueryBuilder query, SortBuilder<?> sort) {

		return searchRequest(query, sort, null);
	}

	protected SearchResponse searchRequest(QueryBuilder query, List<String> returnFields) {

		return searchRequest(query, SortBuilders.fieldSort("id").order(SortOrder.ASC), returnFields);
	}

	protected SearchResponse searchRequest(QueryBuilder query, SortBuilder<?> sort, List<String> returnFields) {

		LOGGER.debug("FindBy query en {} {} con query {} y ordenación.", getIndex(), getType(), query/* + sort.toString() */);

		Integer size = getCount(QueryBuilders.boolQuery().filter(query));

		SearchRequest searchRequest = new SearchRequest(getIndex());

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query).size(size);

		if (sort != null) {
			searchSourceBuilder.sort(sort);
		}
		if (returnFields != null && !returnFields.isEmpty()) {
			searchSourceBuilder.fetchSource(ElasticSearchUtils.getReturnFields(returnFields), null);
		}

		searchRequest.source(searchSourceBuilder);

		searchRequest.types(getType());

		SearchResponse searchResponse;

		LOGGER.debug("searchRequest {} {} -> {}", getIndex(), getType(), searchRequest.toString());

		try {
			searchResponse = ESProvider.getClient().search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESQueryException();
		}

		return searchResponse;
	}

	protected <TQueryDTO extends SimpleQueryDTO> SearchResponse searchRequest(TQueryDTO queryDTO) {

		return searchRequest(queryDTO, null);
	}

	protected <TQueryDTO extends SimpleQueryDTO> SearchResponse searchRequest(TQueryDTO queryDTO, QueryBuilder serviceQuery) {

		SearchRequest searchRequest = new SearchRequest(getIndex());

		SearchSourceBuilder requestBuilder = searchRequestBuilder(queryDTO, serviceQuery);

		searchRequest.source(requestBuilder);

		searchRequest.types(getType());

		SearchResponse searchResponse;

		LOGGER.debug("searchRequest {} {} -> {}", getIndex(), getType(), searchRequest.toString());

		try {
			searchResponse = ESProvider.getClient().search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESQueryException();
		}

		return searchResponse;
	}

	protected <TQueryDTO extends SimpleQueryDTO> SearchSourceBuilder searchRequestBuilder(TQueryDTO queryDTO) {

		return searchRequestBuilder(queryDTO, null);
	}

	protected <TQueryDTO extends SimpleQueryDTO> SearchSourceBuilder searchRequestBuilder(TQueryDTO queryDTO, QueryBuilder serviceQuery) {

		LOGGER.debug("Find en {} {} con queryDTO {} y query interna.", getIndex(), getType(), queryDTO);

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		if (queryDTO.getText() != null
				&& (queryDTO.getText().getSearchFields() == null || queryDTO.getText().getSearchFields().length == 0)) {
			queryDTO.getText().setSearchFields(getDefaultSearchFields());
		}

		BoolQueryBuilder queryBuilder = getQueryBuilder(queryDTO, serviceQuery);
		searchSourceBuilder.query(queryBuilder);

		QueryBuilder postFilter = getPostFilter(queryDTO.getPostFilter());

		if (postFilter != null) {
			searchSourceBuilder.postFilter(postFilter);
		}

		List<BaseAggregationBuilder> aggs = getAggs(queryDTO);

		if (aggs != null) {
			for (BaseAggregationBuilder term : aggs) {
				searchSourceBuilder.aggregation((AggregationBuilder) term);
			}
		}

		if (queryDTO.getText() != null && (queryDTO.getText().getHighlightFields() == null
				|| queryDTO.getText().getHighlightFields().length == 0)) {

			searchSourceBuilder.highlighter(getHighlightBuilder(queryDTO.getText().getHighlightFields()));
		}
		searchSourceBuilder.from(queryDTO.getFrom());
		searchSourceBuilder.size(getSize(queryDTO, queryBuilder));

		List<SortBuilder<?>> sorts = getSorts(queryDTO.getSorts());
		// Finalmente solo en caso de tener una ordenación se añade a la request
		if (sorts != null && !sorts.isEmpty()) {
			for (int i = 0; i < sorts.size(); i++)
				searchSourceBuilder.sort(sorts.get(i));
		}

		List<String> returnFields = queryDTO.getReturnFields();
		if (returnFields != null && !returnFields.isEmpty()) {
			searchSourceBuilder.fetchSource(ElasticSearchUtils.getReturnFields(returnFields), null);
		}

		return searchSourceBuilder;
	}

	protected <TQueryDTO extends SimpleQueryDTO> BoolQueryBuilder getQueryBuilder(TQueryDTO queryDTO, QueryBuilder serviceQuery) {

		QueryBuilder termQuery = getTermQuery(queryDTO.getTerms());

		BoolQueryBuilder partialQuery = null;
		if (serviceQuery != null) {
			partialQuery = QueryBuilders.boolQuery().must(serviceQuery);
		}

		if (termQuery != null && partialQuery != null) {
			partialQuery.must(termQuery);
		} else if (termQuery != null) {
			partialQuery = QueryBuilders.boolQuery().must(termQuery);
		}

		return getQuery(queryDTO, getInternalQuery(), partialQuery);
	}

	private List<SortBuilder<?>> getSorts(List<SortDTO> sortDTOList) {

		// Se obtiene la ordenación enviada desde el exterior
		List<SortBuilder<?>> sorts = ElasticSearchUtils.getSorts(sortDTOList);
		// Si no existe, se establece la de por defecto del servicio en caso de
		// tenerla
		if (sorts == null) {
			return getSort();
		}
		return sorts;
	}

	private HighlightBuilder getHighlightBuilder(String[] highlightFields) {

		// Añade highlightFields por defecto
		if (highlightFields == null || highlightFields.length == 0) {
			highlightFields = getDefaultHighlightFields();
		}
		HighlightBuilder highlightBuilder = new HighlightBuilder();

		for (String field : highlightFields) {
			highlightBuilder.field(field);
		}

		highlightBuilder.preTags(PRETAGS);
		highlightBuilder.postTags(POSTTAGS);

		return highlightBuilder;
	}

	protected MultiSearchResponse getMultiFindResponses(List<SearchSourceBuilder> searchs) {

		MultiSearchRequest request = new MultiSearchRequest();

		for (int i = 0; i < searchs.size(); i++) {
			request.add(new SearchRequest().indices(getIndex()).types(getType()).source(searchs.get(i)));
		}

		LOGGER.debug("getMultiFindResponses {} {} -> {}", getIndex(), getType(), request.toString());

		try {
			return ESProvider.getClient().msearch(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESQueryException();
		}
	}

	protected List<?> scrollQueryReturnItems(QueryBuilder builder, IProcessItemFunction<?> func) {

		List<SortBuilder<?>> sortBuilders = getSort();

		SearchRequest searchRequest = new SearchRequest(getIndex());

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);

		for (SortBuilder<?> sort : sortBuilders) {
			searchSourceBuilder.sort(sort);
		}

		searchSourceBuilder.size(100);

		searchRequest.source(searchSourceBuilder);
		searchRequest.scroll(new TimeValue(60000));
		searchRequest.types(getType());

		SearchResponse searchResponse;

		LOGGER.debug("scrollQueryReturnItems {} {} -> {}", getIndex(), getType(), searchRequest.toString());
		try {
			searchResponse = ESProvider.getClient().search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESQueryException();
		}

		while (true) {

			for (SearchHit hit : searchResponse.getHits().getHits()) {
				func.process(hit);
			}

			SearchScrollRequest scrollRequest = new SearchScrollRequest(searchResponse.getScrollId());
			scrollRequest.scroll(new TimeValue(600000));

			LOGGER.debug("scrollQueryReturnItems {} {} -> {}", getIndex(), getType(), scrollRequest.toString());

			try {
				searchResponse = ESProvider.getClient().scroll(scrollRequest, RequestOptions.DEFAULT);
			} catch (IOException e) {
				e.printStackTrace();
				throw new ESQueryException();
			}

			if (searchResponse.getHits().getHits().length == 0) {
				break;
			}
		}

		return func.getResults();
	}

	/**
	 * Función que sirve de wrapper a la llamada de la util para crear la query de
	 * elastic.
	 *
	 * @param queryDTO
	 *            Dto de la query enviada por el cliente.
	 * @param internalQuery
	 *            Query seteada en el repositorio y que siempre se aplicará.
	 * @param partialQuery
	 *            Query construida a partir de términos y queries que se definen en
	 *            el repositorio específico
	 * @return query de elastic.
	 */
	protected <TQueryDTO extends SimpleQueryDTO> BoolQueryBuilder getQuery(TQueryDTO queryDTO, QueryBuilder internalQuery, QueryBuilder partialQuery) {
		return QueryFactory.getQuery(queryDTO, getInternalQuery(), partialQuery);
	}

	/**
	 * Función que sirve de wrapper a la llamada de la util para crear el postfilter
	 * de elastic
	 *
	 * @param postFilter
	 *            información para crear el postfilter. Es enviada desde el cliente.
	 * @return postFilter de elastic.
	 */
	protected QueryBuilder getPostFilter(Map<String, String[]> postFilter) {
		return ElasticSearchUtils.getPostFilter(postFilter);
	}

	/**
	 * Función que sirve de wrapper a la llamada de la util para crear las
	 * agregaciones.
	 *
	 * @param queryDTO
	 *            Clase enviada desde el cliente con los datos necesarios para crear
	 *            la query.
	 * @return aggs de elastic.
	 */

	protected <TQueryDTO extends SimpleQueryDTO> List<BaseAggregationBuilder> getAggs(TQueryDTO elasticQueryDTO) {
		return ElasticSearchUtils.getAggs(elasticQueryDTO.getAggs());
	}

	/**
	 * Función que devuelve el número total de elementos que se dan como resultado
	 * de aplicar todas las queries activas en el repo.
	 *
	 * @param queryBuilder
	 *            Query completamente construida
	 * @return total de hits.
	 */
	@Override
	public Integer getCount(BoolQueryBuilder queryBuilder) {

		SearchRequest searchRequest = new SearchRequest(INDEX);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		if (queryBuilder == null)
			queryBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.matchAllQuery());

		searchSourceBuilder.query(queryBuilder);
		searchSourceBuilder.size(0);
		searchRequest.source(searchSourceBuilder);
		searchRequest.types(getType());

		LOGGER.debug("getCount {} {} -> {}", getIndex(), getType(), searchRequest.toString());

		SearchResponse queryCount;
		try {
			queryCount = ESProvider.getClient().search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESQueryException();
		}

		long result = queryCount.getHits().getTotalHits();

		return (int) result;
	}

	/**
	 * Función que nos devuelve el size de la query. El size del exterior tiene
	 * preferencia, en caso de no exista, se devuelve todo lo almacenado.
	 *
	 * @param queryDTO.
	 *            queryDto para obtener parámetros de query enviados por el cliente
	 * @param query.
	 *            Query generada para en caso de que no se establezca size, calcular
	 *            el máximo
	 *
	 * @return numero de elementos que devolverá la query
	 */

	protected <TQueryDTO extends SimpleQueryDTO> Integer getSize(TQueryDTO queryDTO, BoolQueryBuilder query) {

		Integer size = queryDTO.getSize();
		if (size == null) {
			size = getCount(query);
			if (size > MAX_SIZE)
				return MAX_SIZE;
		}
		return size;
	}

	/**
	 * Función que nos devuelve una lista de ordenaciones específica para
	 * timeseries. Por defecto, ordena por id.
	 *
	 * @return lista de ordenaciones de elasticsearch
	 */

	protected List<SortBuilder<?>> getSort() {

		List<SortBuilder<?>> sorts = new ArrayList<>();
		sorts.add(SortBuilders.scoreSort().order(SortOrder.DESC));
		return sorts;
	}

	/**
	 * Función que dado un conjunto de términos, nos devuelve una query de
	 * elasticsearch. Debe estar implementado en cada repositorio para darle una
	 * funcionalidad específica y aquí estarán las funcionalidades que comparten
	 * todos los repositorios.
	 *
	 * @param terms
	 *            Map de términos pasados por la query.
	 * @return query de tipo terms de elasticsearch.
	 */
	protected QueryBuilder getTermQuery(Map<String, Object> terms) {
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		return getTermQuery(terms, query);
	}

	/**
	 * Función que dado un conjunto de términos, nos devuelve una query de
	 * elasticsearch. Debe estar implementado en cada repositorio para darle una
	 * funcionalidad específica y aquí estarán las funcionalidades que comparten
	 * todos los repositorios.
	 *
	 * @param terms
	 *            Map de términos pasados por la query.
	 * @param query
	 *            QueryBuilder con la query de los términos acumulados en los
	 *            repositorios específicos.
	 * @return query de tipo terms de elasticsearch.
	 */

	@SuppressWarnings("unchecked")
	protected QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms != null && terms.containsKey("selection")) {
			String selectedId = (String) terms.get("selection");

			Map<String, Object> selected = findSelectedItems(selectedId);

			if (selected != null) {
				List<String> ids = (List<String>) (List<?>) selected.get("ids");
				query.must(QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery("id", ids)));
			}
		}
		return query.hasClauses() ? query : null;
	}

	/**
	 * Función que dado un id de selección devuelve todos los items seleccionados.
	 *
	 * @param selectionId
	 *            identificador de la selección con la que se está trabajando.
	 * @return Map de identificadores de items seleccionados.
	 */
	protected Map<String, Object> findSelectedItems(String selectionId) {

		String selectionIndex = SelectionWorkRepository.INDEX[0];
		String selectionType = SelectionWorkRepository.TYPE;

		GetRequest getRequest = new GetRequest(selectionIndex, selectionType, selectionId);

		GetResponse response;
		try {
			response = ESProvider.getClient().get(getRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ItemNotFoundException("id", selectionId + " en el servicio " + getIndex()[0] + " - " + getType());
		}

		if (response != null && response.isExists()) {
			return response.getSource();
		}
		return null;
	}

	/**
	 * Función para setear una query que siempre se aplicará para el repositorio.
	 *
	 * @param internalQuery
	 *            Query de tipo elasticSearch.
	 */

	protected void setInternalQuery(QueryBuilder internalQuery) {
		this.INTERNAL_QUERY = internalQuery;
	}

	/**
	 * Función para obtener la query de tipo elasticSearch que se debe aplicar
	 * siempre.
	 *
	 * @return Query de tipo elasticSearch.
	 */
	protected QueryBuilder getInternalQuery() {
		return this.INTERNAL_QUERY;
	}

	@SuppressWarnings("unchecked")
	protected List<TModel> findWithNestedReference(String field, String termFilter, Long id) {

		QueryBuilder builder = QueryBuilders.boolQuery().filter(QueryBuilders.nestedQuery(field,
				QueryBuilders.boolQuery().must(QueryBuilders.termQuery(termFilter, id)), ScoreMode.Avg));

		return ((List<TModel>) scrollQueryReturnItems(builder));
	}

	@SuppressWarnings("unchecked")
	protected List<TModel> findWithSpecificReference(String termFilter, Long id) {

		QueryBuilder builder = QueryBuilders.boolQuery()
				.filter(QueryBuilders.boolQuery().must(QueryBuilders.termQuery(termFilter, id)));

		return ((List<TModel>) scrollQueryReturnItems(builder));
	}

	protected abstract List<?> scrollQueryReturnItems(QueryBuilder builder);

	protected <W> W getResponseToWrapper(GetResponse response, JavaType wrapperType) {

		return objectMapper.convertValue(ElasticSearchUtils.getResponsetoObject(response), wrapperType);
	}

	protected <W> W searchResponseToWrapper(SearchResponse response, JavaType wrapperType) {

		return objectMapper.convertValue(ElasticSearchUtils.searchResponsetoObject(response, objectMapper), wrapperType);
	}

	@SuppressWarnings("unchecked")
	protected <W> W mgetResponseToWrapper(MultiGetResponse result, JavaType wrapperType) {

		return (W) ElasticSearchUtils.parseMGetHit(result, wrapperType, objectMapper);
	}

	public SimpleQueryDTO createSimpleQueryDTOFromSuggestQueryParams(String[] fields, String text, Integer size) {

		SimpleQueryDTO queryDTO = new SimpleQueryDTO();

		SuggestQueryDTO suggestQueryDTO = new SuggestQueryDTO();

		suggestQueryDTO.setText(text);
		if (fields != null && fields.length > 0) {
			suggestQueryDTO.setSearchFields(fields);
		} else {
			suggestQueryDTO.setSearchFields(getDefaultSuggestFields());
		}
		if (size != null) {
			suggestQueryDTO.setSize(size);
		}
		queryDTO.setSuggest(suggestQueryDTO);
		return queryDTO;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> convertTModelToSource(TModel modelToIndex) {
		return objectMapper.convertValue(modelToIndex, Map.class);
	}


	protected String getUpdatePropertyScript() {
		return ElasticSearchUtils.getScriptFile(UPDATE_PROPERTY_SCRIPT_PATH);
	}

	protected String getUpdateNestedPropertyScript() {
		return ElasticSearchUtils.getScriptFile(UPDATE_NESTED_PROPERTY_SCRIPT_PATH);
	}

	protected abstract String[] getDefaultSearchFields();

	protected abstract String[] getDefaultHighlightFields();

	protected abstract String[] getDefaultSuggestFields();

	/*
	 * Función para obtener el índice a partir del indice original + un campo de los
	 * datos Solo en series temporales, en otros casos, devolver directamente el
	 * índice.
	 */
	protected String getIndex(TModel modelToIndex) {
		return getIndex()[0];
	}

	@Override
	public String[] getIndex() {
		return INDEX;
	}

	@Override
	public String getType() {
		return TYPE;
	}
}
