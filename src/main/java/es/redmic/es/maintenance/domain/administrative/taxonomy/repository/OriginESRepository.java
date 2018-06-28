package es.redmic.es.maintenance.domain.administrative.taxonomy.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.common.model.DomainES;

@Repository
public class OriginESRepository extends DomainESRepository<DomainES> {
	private static String[] INDEX = { "taxon-domains" };
	private static String[] TYPE = { "origin" };

	public OriginESRepository() {
		super(INDEX, TYPE);
	}
}
