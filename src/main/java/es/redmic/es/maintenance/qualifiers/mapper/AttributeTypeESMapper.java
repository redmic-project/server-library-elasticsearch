package es.redmic.es.maintenance.qualifiers.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.maintenance.qualifiers.service.AttributeTypeESService;
import es.redmic.models.es.common.utils.HierarchicalUtils;
import es.redmic.models.es.maintenance.qualifiers.dto.AttributeTypeDTO;
import es.redmic.models.es.maintenance.qualifiers.model.AttributeType;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class AttributeTypeESMapper extends CustomMapper<AttributeType, AttributeTypeDTO> {

	@Autowired
	AttributeTypeESService attributeTypeESService;

	@Override
	public void mapAtoB(AttributeType a, AttributeTypeDTO b, MappingContext context) {

		String parent = HierarchicalUtils.getParentId(a.getPath());

		if (parent != null)
			b.setParent(attributeTypeESService.get(parent));
	}
}
