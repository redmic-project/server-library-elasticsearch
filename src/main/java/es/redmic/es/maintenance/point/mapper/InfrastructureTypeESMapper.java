package es.redmic.es.maintenance.point.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.maintenance.point.service.InfrastructureTypeESService;
import es.redmic.models.es.common.utils.HierarchicalUtils;
import es.redmic.models.es.maintenance.point.dto.InfrastructureTypeDTO;
import es.redmic.models.es.maintenance.point.model.InfrastructureType;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class InfrastructureTypeESMapper extends CustomMapper<InfrastructureType, InfrastructureTypeDTO> {

	@Autowired
	InfrastructureTypeESService infrastructureTypeESService;

	@Override
	public void mapAtoB(InfrastructureType a, InfrastructureTypeDTO b, MappingContext context) {

		String parent = HierarchicalUtils.getParentId(a.getPath());

		if (parent != null)
			b.setParent(infrastructureTypeESService.get(parent));
	}
}
