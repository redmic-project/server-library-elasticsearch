package es.redmic.es.data.common.mapper;

import org.springframework.stereotype.Component;

import es.redmic.models.es.common.dto.DTOImplementWithMeta;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class DataItemMapper extends CustomMapper<DataHitWrapper, DTOImplementWithMeta> {

	@Override
	public void mapAtoB(DataHitWrapper a, DTOImplementWithMeta b, MappingContext context) {
		
		mapperFacade.map(a.get_source(), b);	
		
		b.get_meta().setScore(a.get_score());
		b.get_meta().setVersion(a.get_version());
		b.get_meta().setHighlight(a.getHighlight());
    }
}