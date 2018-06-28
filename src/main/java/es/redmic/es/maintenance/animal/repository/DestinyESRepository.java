package es.redmic.es.maintenance.animal.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.common.model.DomainES;

@Repository
public class DestinyESRepository extends DomainESRepository<DomainES> {
	private static String[] INDEX = { "taxon-domains" };
	private static String[] TYPE = { "destiny" };

	public DestinyESRepository() {
		super(INDEX, TYPE);
	}
}