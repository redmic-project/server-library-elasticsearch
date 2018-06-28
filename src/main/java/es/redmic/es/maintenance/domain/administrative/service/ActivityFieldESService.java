package es.redmic.es.maintenance.domain.administrative.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.domain.administrative.repository.ActivityFieldESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.administrative.dto.ActivityFieldDTO;

@Service
public class ActivityFieldESService extends DomainESService<DomainES, ActivityFieldDTO> {

	@Autowired
	ActivityTypeESService activityTypeESService;

	@Autowired
	public ActivityFieldESService(ActivityFieldESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		
		activityTypeESService.updateActivityField(reference);
	}
}
