package es.redmic.es.series.common.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JavaType;

import es.redmic.es.common.queryFactory.series.SeriesQueryUtils;
import es.redmic.es.common.repository.RBaseESRepository;
import es.redmic.es.common.service.UserUtilsServiceItfc;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.series.common.model.SeriesCommon;
import es.redmic.models.es.series.common.model.SeriesHitWrapper;
import es.redmic.models.es.series.common.model.SeriesHitsWrapper;
import es.redmic.models.es.series.common.model.SeriesSearchWrapper;

public abstract class RSeriesESRepository<TModel extends SeriesCommon> extends RBaseESRepository<TModel>
		implements IRSeriesESRepository<TModel> {

	@Autowired
	UserUtilsServiceItfc userService;

	protected String defaultField = "value";

	protected String dateTimeField = "date";

	protected int minDocCount = 0;

	public RSeriesESRepository() {
	}

	public RSeriesESRepository(String[] index, String[] type) {
		super(index, type);
	}

	@Override
	public SeriesHitWrapper<?> findById(String id) {

		return findById(id, null, null);
	}

	/*
	 * Sobrescribe método base para añadir query de control de accesso a datos
	 */
	@Override
	public SeriesHitWrapper<?> findById(String id, String parentId, String grandparentId) {

		BoolQueryBuilder query = SeriesQueryUtils.getItemsQuery(id, parentId, grandparentId,
				userService.getAccessibilityControl());

		SeriesSearchWrapper<?> result = findBy(query);

		if (result.getHits() == null || result.getHits().getHits() == null || result.getHits().getHits().size() != 1)
			throw new ItemNotFoundException("id", id);

		return result.getHits().getHits().get(0);
	}

	@Override
	public SeriesHitsWrapper<?> mget(MgetDTO dto) {

		return mget(dto, null, null);
	}

	/*
	 * Sobrescribe método base para añadir query de control de accesso a datos
	 */
	@Override
	public SeriesHitsWrapper<?> mget(MgetDTO dto, String parentId, String grandparentId) {

		List<String> ids = dto.getIds();

		BoolQueryBuilder query = SeriesQueryUtils.getItemsQuery(ids, parentId, grandparentId,
				userService.getAccessibilityControl());

		SeriesSearchWrapper<?> result = findBy(query, dto.getFields());

		if (result.getHits() == null || result.getHits().getHits() == null)
			throw new ItemNotFoundException("ids", dto.getIds().toString());

		if (result.getHits().getHits().size() != ids.size()) {

			for (SeriesHitWrapper<?> hit : result.getHits().getHits()) {
				ids.remove(hit.get_id());
			}

			throw new ItemNotFoundException("ids", ids.toString());
		}

		return result.getHits();
	}

	@Override
	public SeriesSearchWrapper<?> searchByIds(String[] ids) {

		return findBy(QueryBuilders.idsQuery().addIds(ids));
	}

	protected SeriesSearchWrapper<?> findBy(QueryBuilder queryBuilder) {

		return findBy(queryBuilder, null);
	}

	protected SeriesSearchWrapper<?> findBy(QueryBuilder queryBuilder, List<String> returnFields) {

		return searchResponseToWrapper(searchRequest(queryBuilder, returnFields),
				getSourceType(SeriesSearchWrapper.class));
	}

	@Override
	public SeriesSearchWrapper<?> find(DataQueryDTO queryDTO) {
		return find(queryDTO, null, null);
	}

	@Override
	public SeriesSearchWrapper<?> find(DataQueryDTO queryDTO, String parentId, String grandparentId) {

		QueryBuilder serviceQuery = SeriesQueryUtils.getHierarchicalQuery(queryDTO, parentId, grandparentId);

		SearchResponse result = searchRequest(queryDTO, serviceQuery);

		if (result.getFailedShards() > 0)
			return null;

		return searchResponseToWrapper(result, getSourceType(SeriesSearchWrapper.class));
	}

	@Override
	public List<SeriesSearchWrapper<?>> multiFind(List<SearchRequestBuilder> searchs) {

		List<SeriesSearchWrapper<?>> results = new ArrayList<SeriesSearchWrapper<?>>();

		MultiSearchResponse resultRequest = getMultiFindResponses(searchs);

		for (MultiSearchResponse.Item item : resultRequest.getResponses()) {
			SearchResponse response = item.getResponse();
			results.add(searchResponseToWrapper(response, getSourceType(SeriesSearchWrapper.class)));
		}
		return results;
	}

	/**
	 * Función que nos devuelve el size de la query El size del exterior tiene
	 * preferencia, en caso de no exista, se devuelve todo lo almacenado.
	 * 
	 * @param size.
	 *            Size enviado desde queryDto
	 * @param queryDTO.
	 *            queryDto para obtener parámetros de query enviados por el cliente
	 * 
	 * @return numero de elementos que devolverá la query
	 */
	@Override
	protected Integer getSize(DataQueryDTO queryDTO, BoolQueryBuilder query) {

		if ((queryDTO.getAggs() != null && queryDTO.getAggs().size() > 0) || (queryDTO.getInterval() != null))
			return 0;

		return super.getSize(queryDTO, query);
	}

	/**
	 * Función que nos devuelve una lista de ordenaciones específica para
	 * timeseries. Por defecto, ordena por id.
	 * 
	 * @return lista de ordenaciones de elasticsearch
	 */
	@Override
	protected List<SortBuilder<?>> getSort() {

		List<SortBuilder<?>> sorts = new ArrayList<SortBuilder<?>>();
		sorts.add(SortBuilders.fieldSort(dateTimeField).order(SortOrder.ASC));
		return sorts;
	}

	/**
	 * Función que sobrescribe a getTermQuery de RElasticSearchRepository para
	 * añadir implementación específica para crear una query a apartir de una serie
	 * de términos obtenidos por el controlador.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("dataDefinition")) {
			List<Integer> ids = (List<Integer>) (List<?>) terms.get("dataDefinition");
			query.must(QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery("dataDefinition", ids)));
		}
		return super.getTermQuery(terms, query);
	}

	protected DateHistogramAggregationBuilder getDateHistogramAggregation(DateHistogramInterval dateHistogramInterval) {

		return AggregationBuilders.dateHistogram("dateHistogram").field(dateTimeField)
				.dateHistogramInterval(dateHistogramInterval).minDocCount(minDocCount);
	}

	@Override
	protected List<?> scrollQueryReturnItems(QueryBuilder builder) {

		return scrollQueryReturnItems(builder, new SeriesItemsProcessingFunction<TModel>(typeOfTModel, objectMapper));
	}

	@Override
	protected JavaType getSourceType(Class<?> wrapperClass) {
		return objectMapper.getTypeFactory().constructParametricType(wrapperClass, typeOfTModel);
	}

	public SimpleQueryDTO createSimpleQueryDTOFromQueryParams(Integer from, Integer size) {

		SimpleQueryDTO queryDTO = new SimpleQueryDTO();

		if (from != null)
			queryDTO.setFrom(from);
		if (size != null)
			queryDTO.setSize(size);

		return queryDTO;
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "remark" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "remark" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "remark" };
	}
}