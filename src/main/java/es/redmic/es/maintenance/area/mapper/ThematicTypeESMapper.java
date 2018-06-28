package es.redmic.es.maintenance.area.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.maintenance.area.service.ThematicTypeESService;
import es.redmic.models.es.common.utils.HierarchicalUtils;
import es.redmic.models.es.maintenance.areas.dto.ThematicTypeDTO;
import es.redmic.models.es.maintenance.areas.model.ThematicType;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ThematicTypeESMapper extends CustomMapper<ThematicType, ThematicTypeDTO> {

	@Autowired
	ThematicTypeESService thematicTypeESService;

	@Override
	public void mapAtoB(ThematicType a, ThematicTypeDTO b, MappingContext context) {

		String parent = HierarchicalUtils.getParentId(a.getPath());

		if (parent != null)
			b.setParent(thematicTypeESService.get(parent));
	}
}
