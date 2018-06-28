package es.redmic.es.maintenance.parameter.repository;

import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.models.es.maintenance.parameter.model.Parameter;

@Repository
public class ParameterESRepository extends RWDataESRepository<Parameter> {

	private static String[] INDEX = { "parameter-domains" };
	private static String[] TYPE = { "parameter" };

	public ParameterESRepository() {
		super(INDEX, TYPE);
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Parameter> findUnits(String parameterId) {
		return (DataSearchWrapper<Parameter>) findBy(
				QueryBuilders.boolQuery().must(QueryBuilders.idsQuery().addIds(parameterId)));
	}
}
