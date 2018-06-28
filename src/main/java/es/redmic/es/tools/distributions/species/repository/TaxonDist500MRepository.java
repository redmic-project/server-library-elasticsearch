package es.redmic.es.tools.distributions.species.repository;

import org.springframework.stereotype.Repository;

@Repository
public class TaxonDist500MRepository extends RWTaxonDistributionRepository {

	private static String[] INDEX = { "distribution-500" };
	private static String[] TYPE = { "taxon" };
	
	private Integer gridSize = 500;

	public TaxonDist500MRepository() {
		super(INDEX, TYPE);
	}
	
	@Override
	public Integer getGridSize() {
		return gridSize;
	}
}
