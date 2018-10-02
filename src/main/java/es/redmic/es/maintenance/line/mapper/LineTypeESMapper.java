package es.redmic.es.maintenance.line.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.maintenance.line.service.LineTypeESService;
import es.redmic.models.es.common.utils.HierarchicalUtils;
import es.redmic.models.es.maintenance.line.dto.LineTypeDTO;
import es.redmic.models.es.maintenance.line.model.LineType;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class LineTypeESMapper extends CustomMapper<LineType, LineTypeDTO> {

	@Autowired
	LineTypeESService lineTypeESService;

	@Override
	public void mapAtoB(LineType a, LineTypeDTO b, MappingContext context) {

		String parent = HierarchicalUtils.getParentId(a.getPath());

		if (parent != null)
			b.setParent(lineTypeESService.get(parent));
	}
}
