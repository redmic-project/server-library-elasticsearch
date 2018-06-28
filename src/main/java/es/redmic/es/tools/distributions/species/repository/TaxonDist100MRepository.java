package es.redmic.es.tools.distributions.species.repository;

import org.springframework.stereotype.Repository;

@Repository
public class TaxonDist100MRepository extends RWTaxonDistributionRepository {

	private static String[] INDEX = { "distribution-100" };
	private static String[] TYPE = { "taxon" };
	
	private Integer gridSize = 100;

	public TaxonDist100MRepository() {
		super(INDEX, TYPE);
	}
	
	@Override
	public Integer getGridSize() {
		return gridSize;
	}
}
