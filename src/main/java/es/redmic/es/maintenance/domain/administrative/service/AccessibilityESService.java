package es.redmic.es.maintenance.domain.administrative.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.service.ActivityESService;
import es.redmic.es.administrative.service.ProgramESService;
import es.redmic.es.administrative.service.ProjectESService;
import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.domain.administrative.repository.AccessibilityESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.common.dto.AccessibilityDTO;

@Service
public class AccessibilityESService extends DomainESService<DomainES, AccessibilityDTO> {

	@Autowired
	ActivityESService activityESService;

	@Autowired
	ProjectESService projectESService;

	@Autowired
	ProgramESService programESService;

	@Autowired
	public AccessibilityESService(AccessibilityESRepository repository) {
		super(repository);
	}

	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {

		activityESService.updateAccessibility(reference);
		projectESService.updateAccessibility(reference);
		programESService.updateAccessibility(reference);
	}
}
