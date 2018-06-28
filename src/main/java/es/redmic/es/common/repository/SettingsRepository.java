package es.redmic.es.common.repository;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.common.dto.BaseDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.TextQueryDTO;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public class SettingsRepository<TModel extends BaseES<?>, TDTO extends BaseDTO<?>> extends RWDataESRepository<TModel> {

	public SettingsRepository(String[] index, String[] type) {
		super(index, type);
	}

	public DataSearchWrapper<?> findByUserAndSearch(String user, String service, DataQueryDTO dto) {

		BoolQueryBuilder filter = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("userId", user))
				.must(QueryBuilders.termQuery("service", service));

		QueryBuilder query = null;
		if (dto.getText() != null) {
			TextQueryDTO text = dto.getText();
			if (text.getText() != null) {
				query = QueryBuilders.multiMatchQuery(text.getText(), text.getSearchFields());
			}
		} else
			query = QueryBuilders.matchAllQuery();

		return findBy(QueryBuilders.boolQuery().must(query).filter(filter));
	}
}
