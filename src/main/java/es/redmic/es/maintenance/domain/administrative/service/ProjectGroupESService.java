package es.redmic.es.maintenance.domain.administrative.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.service.ProjectESService;
import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.domain.administrative.repository.ProjectGroupESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.administrative.dto.ProjectGroupDTO;

@Service
public class ProjectGroupESService extends DomainESService<DomainES, ProjectGroupDTO> {

	@Autowired
	ProjectESService projectESService;

	@Autowired
	public ProjectGroupESService(ProjectGroupESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		projectESService.updateProjectGroup(reference);
	}
}
