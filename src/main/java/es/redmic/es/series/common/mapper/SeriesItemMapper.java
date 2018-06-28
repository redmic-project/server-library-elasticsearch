package es.redmic.es.series.common.mapper;

import org.springframework.stereotype.Component;

import es.redmic.models.es.common.dto.DTOImplementWithMeta;
import es.redmic.models.es.series.common.model.SeriesHitWrapper;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class SeriesItemMapper extends CustomMapper<SeriesHitWrapper, DTOImplementWithMeta> {

	@Override
	public void mapAtoB(SeriesHitWrapper a, DTOImplementWithMeta b, MappingContext context) {
		
		mapperFacade.map(a.get_source(), b);	
		
		b.get_meta().setScore(a.get_score());
		b.get_meta().setVersion(a.get_version());
		b.get_meta().setHighlight(a.getHighlight());
    }
}