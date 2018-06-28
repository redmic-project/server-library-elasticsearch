package es.redmic.es.tools.distributions.species.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.geodata.common.service.GridServiceItfc;
import es.redmic.es.tools.distributions.species.repository.TaxonDist500MRepository;

@Service
public class TaxonDist500MService extends RWTaxonDistributionService {

	@Autowired
	public TaxonDist500MService(TaxonDist500MRepository repository, GridServiceItfc gridUtil) {
		super(repository, gridUtil);
	}

}
