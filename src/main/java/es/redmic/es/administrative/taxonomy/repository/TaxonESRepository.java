package es.redmic.es.administrative.taxonomy.repository;

import org.springframework.stereotype.Repository;

import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;

@Repository
public class TaxonESRepository extends HierarchicalTaxonomyESRepository<Taxon, TaxonDTO> {

	public TaxonESRepository() {
	}
}
