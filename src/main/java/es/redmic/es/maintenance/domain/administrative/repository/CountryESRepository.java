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
	
	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "name", "name.suggest", "code", "code.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "name", "name.suggest", "code", "code.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "name", "code" };
	}
}
