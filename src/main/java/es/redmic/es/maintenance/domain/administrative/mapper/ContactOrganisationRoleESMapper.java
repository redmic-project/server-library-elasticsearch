package es.redmic.es.maintenance.domain.administrative.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.ContactESService;
import es.redmic.es.administrative.service.OrganisationESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.ContactRoleESService;
import es.redmic.models.es.administrative.dto.ContactDTO;
import es.redmic.models.es.administrative.dto.ContactOrganisationRoleDTO;
import es.redmic.models.es.administrative.dto.OrganisationDTO;
import es.redmic.models.es.administrative.model.ContactCompact;
import es.redmic.models.es.administrative.model.OrganisationCompact;
import es.redmic.models.es.administrative.model.OrganisationContactRoles;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.RoleDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ContactOrganisationRoleESMapper extends CustomMapper<OrganisationContactRoles, ContactOrganisationRoleDTO> {

	@Autowired
	ContactESService contactESService;

	@Autowired
	OrganisationESService organisationESService;

	@Autowired
	ContactRoleESService contactRoleESService;

	@Override
	public void mapAtoB(OrganisationContactRoles a, ContactOrganisationRoleDTO b, MappingContext context) {

		b.setContact(mapperFacade.map(a.getContact(), ContactDTO.class));
		b.setOrganisation(mapperFacade.map(a.getOrganisation(), OrganisationDTO.class));
		b.setRole(mapperFacade.map(a.getRole(), RoleDTO.class));
	}

	@Override
	public void mapBtoA(ContactOrganisationRoleDTO b, OrganisationContactRoles a, MappingContext context) {

		a.setContact((ContactCompact) mapperFacade.newObject(b.getContact(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(contactESService)));
		a.setOrganisation((OrganisationCompact) mapperFacade.newObject(b.getOrganisation(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(organisationESService)));
		a.setRole((DomainES) mapperFacade.newObject(b.getRole(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(contactRoleESService)));
	}
}
