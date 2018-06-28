package es.redmic.es.maintenance.animal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.taxonomy.service.AnimalESService;
import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.animal.repository.EndingESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.animal.dto.EndingDTO;

@Service
public class EndingESService extends DomainESService<DomainES, EndingDTO> {

	@Autowired
	AnimalESService animalESService;
	
	@Autowired
	public EndingESService(EndingESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		
		animalESService.updateEnding(reference);
	}
}
