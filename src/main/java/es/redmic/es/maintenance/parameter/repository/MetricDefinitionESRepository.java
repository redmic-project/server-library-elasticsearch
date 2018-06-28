package es.redmic.es.maintenance.parameter.repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.maintenance.parameter.model.MetricDefinition;

//@Repository
public class MetricDefinitionESRepository extends RWDataESRepository<MetricDefinition> {

	private static String[] INDEX = { "parameter-domains" };
	private static String[] TYPE = { "metricdefinition" };

	public MetricDefinitionESRepository() {
		super(INDEX, TYPE);
	}
}
