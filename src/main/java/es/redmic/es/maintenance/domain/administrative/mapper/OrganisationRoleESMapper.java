package es.redmic.es.maintenance.domain.administrative.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.OrganisationESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.OrganisationRoleESService;
import es.redmic.models.es.administrative.dto.OrganisationCompactDTO;
import es.redmic.models.es.administrative.dto.OrganisationRoleDTO;
import es.redmic.models.es.administrative.model.OrganisationCompact;
import es.redmic.models.es.administrative.model.OrganisationRoles;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.RoleDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class OrganisationRoleESMapper extends CustomMapper<OrganisationRoles, OrganisationRoleDTO> {

	@Autowired
	private OrganisationESService organisationESService;

	@Autowired
	private OrganisationRoleESService organisationRoleESService;

	@Override
	public void mapAtoB(OrganisationRoles a, OrganisationRoleDTO b, MappingContext context) {

		b.setOrganisation(mapperFacade.map(a.getOrganisation(), OrganisationCompactDTO.class));
		b.setRole(mapperFacade.map(a.getRole(), RoleDTO.class));
	}

	@Override
	public void mapBtoA(OrganisationRoleDTO b, OrganisationRoles a, MappingContext context) {

		a.setOrganisation(mapperFacade.map(mapperFacade.newObject(b.getOrganisation(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(organisationESService)), OrganisationCompact.class));
		a.setRole((DomainES) mapperFacade.newObject(b.getRole(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(organisationRoleESService)));
	}
}
