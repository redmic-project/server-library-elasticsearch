package es.redmic.es.maintenance.objects.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.maintenance.objects.service.ObjectTypeESService;
import es.redmic.models.es.common.utils.HierarchicalUtils;
import es.redmic.models.es.maintenance.objects.dto.ObjectTypeDTO;
import es.redmic.models.es.maintenance.objects.model.ObjectType;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ObjectTypeESMapper extends CustomMapper<ObjectType, ObjectTypeDTO> {

	@Autowired
	ObjectTypeESService objectTypeESService;

	@Override
	public void mapAtoB(ObjectType a, ObjectTypeDTO b, MappingContext context) {

		String parent = HierarchicalUtils.getParentId(a.getPath());

		if (parent != null)
			b.setParent(objectTypeESService.get(parent));
	}
}
