package es.redmic.es.administrative.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.OrganisationESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.PlatformTypeESService;
import es.redmic.models.es.administrative.dto.ContactOrganisationRoleDTO;
import es.redmic.models.es.administrative.dto.OrganisationDTO;
import es.redmic.models.es.administrative.dto.PlatformDTO;
import es.redmic.models.es.administrative.model.OrganisationCompact;
import es.redmic.models.es.administrative.model.OrganisationContactRoles;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.PlatformTypeDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class PlatformESMapper extends CustomMapper<Platform, PlatformDTO> {

	@Autowired
	PlatformTypeESService platformTypeESService;

	@Autowired
	OrganisationESService organisationESService;

	@Override
	public void mapAtoB(Platform a, PlatformDTO b, MappingContext context) {

		b.setPlatformType(mapperFacade.map(a.getPlatformType(), PlatformTypeDTO.class));
		if (a.getOrganisation() != null)
			b.setOrganisation(mapperFacade.map(b.getOrganisation(), OrganisationDTO.class));
		b.setContacts(mapperFacade.mapAsList(a.getContacts(), ContactOrganisationRoleDTO.class));
	}

	@Override
	public void mapBtoA(PlatformDTO b, Platform a, MappingContext context) {

		a.setPlatformType((DomainES) mapperFacade.newObject(b.getPlatformType(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(platformTypeESService)));
		if (b.getOrganisation() != null)
			a.setOrganisation((OrganisationCompact) mapperFacade.map(mapperFacade.newObject(b.getOrganisation(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(organisationESService)), OrganisationCompact.class));
		a.setContacts(mapperFacade.mapAsList(b.getContacts(), OrganisationContactRoles.class));
	}
}
