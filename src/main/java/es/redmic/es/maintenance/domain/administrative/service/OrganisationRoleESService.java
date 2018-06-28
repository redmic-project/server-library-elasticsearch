package es.redmic.es.maintenance.domain.administrative.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.service.ActivityESService;
import es.redmic.es.administrative.service.ProgramESService;
import es.redmic.es.administrative.service.ProjectESService;
import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.domain.administrative.repository.OrganisationRoleESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.administrative.dto.OrganisationRoleDTO;

@Service
public class OrganisationRoleESService extends DomainESService<DomainES, OrganisationRoleDTO> {

	@Autowired
	ActivityESService activityESService;
	
	@Autowired
	ProjectESService projectESService;
	
	@Autowired
	ProgramESService programESService;
	
	@Autowired
	public OrganisationRoleESService(OrganisationRoleESRepository repository) {
		super(repository);
	}

	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		
		activityESService.updateOrganisationRoles(reference);
		projectESService.updateOrganisationRoles(reference);
		programESService.updateOrganisationRoles(reference);
	};
}
