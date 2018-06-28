package es.redmic.es.common.repository;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

public interface IRBaseESRepository<TModel extends BaseES<?>> {

	public List<String> suggest(DataQueryDTO queryDTO);
	public List<String> suggest(DataQueryDTO queryDTO, QueryBuilder serviceQuery);
	public Integer getCount(BoolQueryBuilder queryBuilder);
	public String[] getIndex();
	public String[] getType();
}