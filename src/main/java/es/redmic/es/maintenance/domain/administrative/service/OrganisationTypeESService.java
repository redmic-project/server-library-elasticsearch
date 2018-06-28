package es.redmic.es.maintenance.domain.administrative.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.service.OrganisationESService;
import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.domain.administrative.repository.OrganisationTypeESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.administrative.dto.OrganisationTypeDTO;

@Service
public class OrganisationTypeESService extends DomainESService<DomainES, OrganisationTypeDTO> {

	@Autowired
	OrganisationESService organisationESService;

	@Autowired
	public OrganisationTypeESService(OrganisationTypeESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		organisationESService.updateOrganisationType(reference);
	}
}
