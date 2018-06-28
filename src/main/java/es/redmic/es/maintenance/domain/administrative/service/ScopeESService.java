package es.redmic.es.maintenance.domain.administrative.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.service.ActivityESService;
import es.redmic.es.administrative.service.ProgramESService;
import es.redmic.es.administrative.service.ProjectESService;
import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.domain.administrative.repository.ScopeESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.administrative.dto.ScopeDTO;

@Service
public class ScopeESService extends DomainESService<DomainES, ScopeDTO> {

	@Autowired
	ActivityESService activityESService;

	@Autowired
	ProjectESService projectESService;

	@Autowired
	ProgramESService programESService;

	@Autowired
	public ScopeESService(ScopeESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		activityESService.updateScope(reference);
		projectESService.updateScope(reference);
		programESService.updateScope(reference);
	}
}
