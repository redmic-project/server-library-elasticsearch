package es.redmic.es.data.common.mapper;

import org.springframework.stereotype.Component;

import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.data.common.model.DataHitsWrapper;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class DataCollectionMapper extends CustomMapper<DataHitsWrapper, JSONCollectionDTO> {

	@SuppressWarnings("unchecked")
	@Override
	public void mapAtoB(DataHitsWrapper a, JSONCollectionDTO b, MappingContext context) {

		Class<?> targetTypeDto = (Class<?>) context.getProperty("targetTypeDto");

		if (targetTypeDto == null)
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);

		b.setData(mapperFacade.mapAsList(a.getHits(), targetTypeDto, context));
		b.get_meta().setMax_score(a.getMax_score());
		b.setTotal(a.getTotal());
	}
}