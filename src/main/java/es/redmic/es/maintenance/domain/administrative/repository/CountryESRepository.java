package es.redmic.es.maintenance.domain.administrative.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.maintenance.administrative.model.Country;

@Repository
public class CountryESRepository extends DomainESRepository<Country> {
	private static String[] INDEX = { "administrative-domains" };
	private static String[] TYPE = { "country" };

	public CountryESRepository() {
		super(INDEX, TYPE);
	}
}
