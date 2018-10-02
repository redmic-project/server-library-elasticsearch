package es.redmic.es.common.repository;

import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public class DomainESRepository<TModel extends BaseES<Long>> extends RWDataESRepository<TModel> {

	public DomainESRepository(String[] index, String[] type) {
		super(index, type);
	}

	public DataSearchWrapper<?> findByName(String name) {

		return findBy(QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("name", name)));
	}

	public DataSearchWrapper<?> findByName_en(String name) {

		return findBy(QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("name_en", name)));
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
}
