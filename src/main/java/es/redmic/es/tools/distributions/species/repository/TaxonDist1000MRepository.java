package es.redmic.es.tools.distributions.species.repository;

import org.springframework.stereotype.Repository;

@Repository
public class TaxonDist1000MRepository extends RWTaxonDistributionRepository {

	private static String[] INDEX = { "distribution-1000" };
	private static String[] TYPE = { "taxon" };
	
	private Integer gridSize = 1000;

	public TaxonDist1000MRepository() {
		super(INDEX, TYPE);
	}
	
	@Override
	public Integer getGridSize() {
		return gridSize;
	}
}
