package es.redmic.es.maintenance.domain.administrative.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.ActivityFieldESService;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.ActivityFieldDTO;
import es.redmic.models.es.maintenance.administrative.dto.ActivityTypeDTO;
import es.redmic.models.es.maintenance.administrative.model.ActivityType;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ActivityTypeESMapper extends CustomMapper<ActivityType, ActivityTypeDTO> {

	@Autowired
	ActivityFieldESService activityFieldService;

	@Override
	public void mapAtoB(ActivityType a, ActivityTypeDTO b, MappingContext context) {

		b.setActivityField(mapperFacade.map(a.getActivityField(), ActivityFieldDTO.class));
	}

	@Override
	public void mapBtoA(ActivityTypeDTO b, ActivityType a, MappingContext context) {

		a.setActivityField((DomainES) mapperFacade.newObject(b.getActivityField(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(activityFieldService)));
	}
}
