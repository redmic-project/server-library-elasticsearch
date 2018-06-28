package es.redmic.es.administrative.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.administrative.model.Organisation;

@Repository
public class OrganisationESRepository extends RWDataESRepository<Organisation> {

	private static String[] INDEX = { "administrative" };
	private static String[] TYPE = { "organisation" };

	public OrganisationESRepository() {
		super(INDEX, TYPE);
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "name", "name.suggest", "acronym", "acronym.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "name", "name.suggest", "acronym", "acronym.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "name", "acronym" };
	}
}
