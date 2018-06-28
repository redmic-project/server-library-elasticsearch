package es.redmic.es.maintenance.domain.administrative.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.ContactESService;
import es.redmic.es.administrative.service.PlatformESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.ContactRoleESService;
import es.redmic.models.es.administrative.dto.ActivityPlatformRoleDTO;
import es.redmic.models.es.administrative.dto.ContactBaseDTO;
import es.redmic.models.es.administrative.dto.PlatformDTO;
import es.redmic.models.es.administrative.model.ContactCompact;
import es.redmic.models.es.administrative.model.PlatformCompact;
import es.redmic.models.es.administrative.model.PlatformContactRoles;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.RoleDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class PlatformContactRoleESMapper extends CustomMapper<PlatformContactRoles, ActivityPlatformRoleDTO> {

	@Autowired
	ContactESService contactESService;

	@Autowired
	private PlatformESService platformESService;

	@Autowired
	ContactRoleESService contactRoleESService;

	@Override
	public void mapAtoB(PlatformContactRoles a, ActivityPlatformRoleDTO b, MappingContext context) {

		b.setContact(mapperFacade.map(a.getContact(), ContactBaseDTO.class));
		b.setRole(mapperFacade.map(a.getRole(), RoleDTO.class));
		b.setPlatform(mapperFacade.map(a.getPlatform(), PlatformDTO.class));
	}

	@Override
	public void mapBtoA(ActivityPlatformRoleDTO b, PlatformContactRoles a, MappingContext context) {

		a.setContact(mapperFacade.map(mapperFacade.newObject(b.getContact(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(contactESService)), ContactCompact.class));
		a.setPlatform(mapperFacade.map(mapperFacade.newObject(b.getPlatform(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(platformESService)), PlatformCompact.class));
		a.setRole((DomainES) mapperFacade.newObject(b.getRole(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(contactRoleESService)));
	}
}
