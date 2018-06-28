package es.redmic.es.administrative.taxonomy.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.administrative.taxonomy.model.Misidentification;

@Repository
public class MisidentificationESRepository extends RWDataESRepository<Misidentification> {

	private static String[] INDEX = { "taxons" };
	private static String[] TYPE = { "misidentification" };

	public MisidentificationESRepository() {
		super(INDEX, TYPE);
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "taxon.scientificName.suggest", "badIdentity.scientificName.suggest",
				"document.title.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "taxon.scientificName.suggest", "badIdentity.scientificName.suggest",
				"document.title.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "taxon.scientificName", "badIdentity.scientificName", "document.title" };
	}
}