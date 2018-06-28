package es.redmic.es.tools.distributions.species.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.geodata.common.service.GridServiceItfc;
import es.redmic.es.tools.distributions.species.repository.TaxonDist5000MRepository;

@Service
public class TaxonDist5000MService extends RWTaxonDistributionService {

	@Autowired
	public TaxonDist5000MService(TaxonDist5000MRepository repository, GridServiceItfc gridUtil) {
		super(repository, gridUtil);
	}

}
