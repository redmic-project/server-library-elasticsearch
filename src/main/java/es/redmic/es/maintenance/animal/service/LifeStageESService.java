package es.redmic.es.maintenance.animal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.taxonomy.service.AnimalESService;
import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.animal.repository.LifeStageESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.animal.dto.LifeStageDTO;

@Service
public class LifeStageESService extends DomainESService<DomainES, LifeStageDTO> {
	
	@Autowired
	AnimalESService animalESService;

	@Autowired
	public LifeStageESService(LifeStageESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		
		animalESService.updateLifeStage(reference);
	}
}
