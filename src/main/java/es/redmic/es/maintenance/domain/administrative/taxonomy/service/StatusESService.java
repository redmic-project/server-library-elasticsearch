package es.redmic.es.maintenance.domain.administrative.taxonomy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.StatusESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.taxonomy.dto.StatusDTO;

@Service
public class StatusESService extends DomainESService<DomainES, StatusDTO> {

	@Autowired
	@Qualifier("TaxonServiceES")
	TaxonESService taxonESService;

	@Autowired
	public StatusESService(StatusESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		taxonESService.updateStatus(reference);
	}
}
