package es.redmic.es.data.common.repository;

import java.util.List;

import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.fasterxml.jackson.databind.JavaType;

import es.redmic.es.common.repository.RBaseESRepository;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.common.query.dto.TextQueryDTO;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.data.common.model.DataHitsWrapper;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public abstract class RDataESRepository<TModel extends BaseES<?>> extends RBaseESRepository<TModel>
		implements IRDataESRepository<TModel> {

	public RDataESRepository() {
	}

	public RDataESRepository(String[] index, String[] type) {
		super(index, type);
	}

	@Override
	public DataHitWrapper<?> findById(String id) {

		return getResponseToWrapper(getRequest(id), getSourceType(DataHitWrapper.class));
	}

	@Override
	public DataSearchWrapper<?> searchByIds(String[] ids) {
		return findBy(QueryBuilders.idsQuery().addIds(ids));
	}

	protected DataSearchWrapper<?> findBy(QueryBuilder queryBuilder) {

		return findBy(queryBuilder, SortBuilders.fieldSort("id").order(SortOrder.ASC), null);
	}

	protected DataSearchWrapper<?> findBy(QueryBuilder queryBuilder, SortBuilder<?> sort) {

		return searchResponseToWrapper(searchRequest(queryBuilder, sort, null), getSourceType(DataSearchWrapper.class));
	}

	protected DataSearchWrapper<?> findBy(QueryBuilder queryBuilder, List<String> returnFields) {

		return findBy(queryBuilder, SortBuilders.fieldSort("id").order(SortOrder.ASC), returnFields);
	}

	protected DataSearchWrapper<?> findBy(QueryBuilder queryBuilder, SortBuilder<?> sort, List<String> returnFields) {

		return searchResponseToWrapper(searchRequest(queryBuilder, sort, returnFields),
				getSourceType(DataSearchWrapper.class));
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DataHitsWrapper<?> mget(MgetDTO dto) {

		MultiGetResponse result = multigetRequest(dto);

		List<DataHitWrapper<?>> hits = mgetResponseToWrapper(result, getSourceType(DataHitWrapper.class));
		DataHitsWrapper data = new DataHitsWrapper(hits);
		data.setTotal(hits.size());
		return data;
	}

	@Override
	public DataSearchWrapper<?> find(DataQueryDTO queryDTO) {

		return searchResponseToWrapper(searchRequest(queryDTO), getSourceType(DataSearchWrapper.class));
	}

	@Override
	protected List<?> scrollQueryReturnItems(QueryBuilder builder) {

		return scrollQueryReturnItems(builder, new DataItemsProcessingFunction<TModel>(typeOfTModel, objectMapper));
	}

	public SimpleQueryDTO createSimpleQueryDTOFromTextQueryParams(String[] fields, String text, Integer from,
			Integer size) {

		SimpleQueryDTO queryDTO = new SimpleQueryDTO();

		if (text != null) {
			TextQueryDTO textDTO = new TextQueryDTO();
			textDTO.setText(text);
			if (fields != null && fields.length > 0)
				textDTO.setHighlightFields(fields);
			else
				textDTO.setSearchFields(getDefaultSearchFields());
			textDTO.setHighlightFields(getDefaultHighlightFields());
			queryDTO.setText(textDTO);
		}
		if (from != null)
			queryDTO.setFrom(from);
		if (size != null)
			queryDTO.setSize(size);

		return queryDTO;
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "name", "name.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "name", "name.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "name" };
	}

	@Override
	protected JavaType getSourceType(Class<?> wrapperClass) {
		return objectMapper.getTypeFactory().constructParametricType(wrapperClass, typeOfTModel);
	}
}