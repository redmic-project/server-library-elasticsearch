package es.redmic.es.maintenance.quality.mapper;

import org.springframework.stereotype.Component;

import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.maintenance.quality.dto.QFlagDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class QFlagHitESMapper extends CustomMapper<DataHitWrapper, QFlagDTO> {

	@Override
	public void mapAtoB(DataHitWrapper a, QFlagDTO b, MappingContext context) {
		mapperFacade.map(a.get_source(), b);
	}
}
