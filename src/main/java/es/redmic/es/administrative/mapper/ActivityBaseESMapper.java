package es.redmic.es.administrative.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.AccessibilityESService;
import es.redmic.es.maintenance.domain.administrative.service.ScopeESService;
import es.redmic.models.es.administrative.dto.ActivityBaseDTO;
import es.redmic.models.es.administrative.dto.ActivityDocumentDTO;
import es.redmic.models.es.administrative.dto.ActivityPlatformRoleDTO;
import es.redmic.models.es.administrative.dto.ContactOrganisationRoleDTO;
import es.redmic.models.es.administrative.dto.OrganisationRoleDTO;
import es.redmic.models.es.administrative.model.ActivityBase;
import es.redmic.models.es.administrative.model.ActivityDocument;
import es.redmic.models.es.administrative.model.ContactOrganisationRoles;
import es.redmic.models.es.administrative.model.OrganisationRoles;
import es.redmic.models.es.administrative.model.PlatformContactRoles;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.ScopeDTO;
import es.redmic.models.es.maintenance.common.dto.AccessibilityDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ActivityBaseESMapper<TModel extends ActivityBase, TDTO extends ActivityBaseDTO> extends CustomMapper<TModel, TDTO> {

	@Autowired
	AccessibilityESService accessibilityESService;
	
	@Autowired
	private ScopeESService scopeESService;

	@Override
	public void mapAtoB(TModel a, TDTO b, MappingContext context) {

		b.setAccessibility(mapperFacade.map(a.getAccessibility(), AccessibilityDTO.class));
		
		if (a.getScope() != null)
			b.setScope(mapperFacade.map(a.getScope(), ScopeDTO.class));
		
		if (a.getContacts() != null)
			b.setContacts(mapperFacade.mapAsList(a.getContacts(), ContactOrganisationRoleDTO.class));
		
		if (a.getDocuments() != null)
			b.setDocuments(mapperFacade.mapAsList(a.getDocuments(), ActivityDocumentDTO.class));
		
		if (a.getOrganisations() != null)
			b.setOrganisations(mapperFacade.mapAsList(a.getOrganisations(), OrganisationRoleDTO.class));
		
		if (a.getPlatforms() != null)
			b.setPlatforms(mapperFacade.mapAsList(a.getPlatforms(), ActivityPlatformRoleDTO.class));
	}

	@Override
	public void mapBtoA(TDTO b, TModel a, MappingContext context) {

		a.setAccessibility((DomainES) mapperFacade.newObject(b.getAccessibility(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(accessibilityESService)));
		
		if (b.getScope() != null)
			a.setScope((DomainES) mapperFacade.newObject(b.getScope(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(scopeESService)));
		
		a.setContacts(mapperFacade.mapAsList(b.getContacts(), ContactOrganisationRoles.class));
		a.setDocuments(mapperFacade.mapAsList(b.getDocuments(), ActivityDocument.class));
		a.setOrganisations(mapperFacade.mapAsList(b.getOrganisations(), OrganisationRoles.class));
		a.setPlatforms(mapperFacade.mapAsList(b.getPlatforms(), PlatformContactRoles.class));
	}
}
