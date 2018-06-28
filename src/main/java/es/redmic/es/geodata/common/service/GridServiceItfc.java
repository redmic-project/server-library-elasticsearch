package es.redmic.es.geodata.common.service;

import com.vividsolutions.jts.geom.Geometry;

import es.redmic.es.tools.distributions.species.repository.RWTaxonDistributionRepository;
import es.redmic.models.es.tools.distribution.model.Distribution;

public interface GridServiceItfc {

	public Distribution getDistribution(Geometry geometry, RWTaxonDistributionRepository taxonDistributionRepository, int intValue);

}
