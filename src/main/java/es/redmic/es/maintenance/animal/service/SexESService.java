package es.redmic.es.maintenance.animal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.taxonomy.service.AnimalESService;
import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.animal.repository.SexESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.animal.dto.SexDTO;

@Service
public class SexESService extends DomainESService<DomainES, SexDTO> {

	@Autowired
	AnimalESService animalESService;
	
	@Autowired
	public SexESService(SexESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		
		animalESService.updateSex(reference);
	}
}