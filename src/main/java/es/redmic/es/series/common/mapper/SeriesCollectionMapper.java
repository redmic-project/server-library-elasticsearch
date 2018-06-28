package es.redmic.es.series.common.mapper;

import org.springframework.stereotype.Component;

import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.series.common.model.SeriesHitsWrapper;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class SeriesCollectionMapper extends CustomMapper<SeriesHitsWrapper, JSONCollectionDTO> {

	@SuppressWarnings("unchecked")
	@Override
	public void mapAtoB(SeriesHitsWrapper a, JSONCollectionDTO b, MappingContext context) {

		Class<?> targetTypeDto = (Class<?>) context.getProperty("targetTypeDto");

		if (targetTypeDto == null)
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);

		b.setData(mapperFacade.mapAsList(a.getHits(), targetTypeDto, context));
		b.get_meta().setMax_score(a.getMax_score());
	}
}