package es.redmic.es.maintenance.strech.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.common.model.DomainES;

@Repository
public class CensingStatusESRepository extends DomainESRepository<DomainES> {
	private static String[] INDEX = { "classification-domains" };
	private static String[] TYPE = { "censingstatus" };

	public CensingStatusESRepository() {
		super(INDEX, TYPE);
	}
}