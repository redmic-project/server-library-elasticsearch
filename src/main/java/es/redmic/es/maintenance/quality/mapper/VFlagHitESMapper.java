package es.redmic.es.maintenance.quality.mapper;

import org.springframework.stereotype.Component;

import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.maintenance.quality.dto.VFlagDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class VFlagHitESMapper extends CustomMapper<DataHitWrapper, VFlagDTO> {

	@Override
	public void mapAtoB(DataHitWrapper a, VFlagDTO b, MappingContext context) {
		
		mapperFacade.map(a.get_source(), b);	
	}
}
