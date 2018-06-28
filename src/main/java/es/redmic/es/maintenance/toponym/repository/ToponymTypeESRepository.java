package es.redmic.es.maintenance.toponym.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.common.model.DomainES;

@Repository
public class ToponymTypeESRepository extends DomainESRepository<DomainES> {

	private static String[] INDEX = { "administrative-domains" };
	private static String[] TYPE = { "toponymtype" };

	public ToponymTypeESRepository() {
		super(INDEX, TYPE);
	}
}
