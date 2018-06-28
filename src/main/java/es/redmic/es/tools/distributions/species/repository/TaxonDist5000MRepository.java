package es.redmic.es.tools.distributions.species.repository;

import org.springframework.stereotype.Repository;

@Repository
public class TaxonDist5000MRepository extends RWTaxonDistributionRepository {

	private static String[] INDEX = { "distribution-5000" };
	private static String[] TYPE = { "taxon" };
	
	private Integer gridSize = 5000;

	public TaxonDist5000MRepository() {
		super(INDEX, TYPE);
	}
	
	@Override
	public Integer getGridSize() {
		return gridSize;
	}
}
