package es.redmic.es.maintenance.domain.administrative.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.DocumentESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.models.es.administrative.dto.ActivityDocumentDTO;
import es.redmic.models.es.administrative.dto.DocumentCompactDTO;
import es.redmic.models.es.administrative.model.ActivityDocument;
import es.redmic.models.es.administrative.model.DocumentCompact;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ActivityDocumentESMapper extends CustomMapper<ActivityDocument, ActivityDocumentDTO> {

	@Autowired
	DocumentESService documentESService;

	@Override
	public void mapAtoB(ActivityDocument a, ActivityDocumentDTO b, MappingContext context) {

		b.setDocument(mapperFacade.map(a.getDocument(), DocumentCompactDTO.class));
	}

	@Override
	public void mapBtoA(ActivityDocumentDTO b, ActivityDocument a, MappingContext context) {

		a.setDocument(mapperFacade.map(mapperFacade.newObject(b.getDocument(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(documentESService)), DocumentCompact.class));
	}
}
