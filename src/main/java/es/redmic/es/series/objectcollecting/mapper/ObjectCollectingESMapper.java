package es.redmic.es.series.objectcollecting.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.objects.service.ObjectTypeESService;
import es.redmic.models.es.maintenance.objects.dto.ObjectCollectingDTO;
import es.redmic.models.es.maintenance.objects.dto.ObjectTypeBaseDTO;
import es.redmic.models.es.maintenance.objects.model.ObjectCollecting;
import es.redmic.models.es.maintenance.objects.model.ObjectTypeBase;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ObjectCollectingESMapper extends CustomMapper<ObjectCollecting, ObjectCollectingDTO> {
	
	@Autowired
	ObjectTypeESService objectService;
	
	@Override
	public void mapAtoB(ObjectCollecting a, ObjectCollectingDTO b, MappingContext context) {
		
		b.setObjectType(mapperFacade.map(a.getObjectType(), ObjectTypeBaseDTO.class));
	}
	
	@Override
	public void mapBtoA(ObjectCollectingDTO b, ObjectCollecting a, MappingContext context) {
				
		a.setObjectType(mapperFacade.map(mapperFacade.newObject(b.getObjectType(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(objectService)), ObjectTypeBase.class));
				
	}
}