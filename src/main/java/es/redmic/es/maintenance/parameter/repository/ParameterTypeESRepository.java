package es.redmic.es.maintenance.parameter.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.common.model.DomainES;

@Repository
public class ParameterTypeESRepository extends DomainESRepository<DomainES> {
	private static String[] INDEX = { "parameter-domains" };
	private static String[] TYPE = { "parametertype" };

	public ParameterTypeESRepository() {
		super(INDEX, TYPE);
	}
}
