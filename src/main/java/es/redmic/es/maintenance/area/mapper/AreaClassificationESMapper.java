package es.redmic.es.maintenance.area.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.area.service.ThematicTypeESService;
import es.redmic.models.es.maintenance.areas.dto.AreaClassificationDTO;
import es.redmic.models.es.maintenance.areas.dto.ThematicTypeBaseDTO;
import es.redmic.models.es.maintenance.areas.model.AreaClassification;
import es.redmic.models.es.maintenance.common.model.ClassificationBase;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class AreaClassificationESMapper extends CustomMapper<AreaClassification, AreaClassificationDTO> {

	@Autowired
	ThematicTypeESService thematicTypeESService;

	@Override
	public void mapAtoB(AreaClassification a, AreaClassificationDTO b, MappingContext context) {

		if (a.getType() != null) {
			b.setType(mapperFacade.map(a.getType(), ThematicTypeBaseDTO.class));
		}
	}

	@Override
	public void mapBtoA(AreaClassificationDTO b, AreaClassification a, MappingContext context) {

		if (b.getType() != null) {
			a.setType(mapperFacade.map(mapperFacade.newObject(b.getType(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(thematicTypeESService)), ClassificationBase.class));
		}
	}
}
