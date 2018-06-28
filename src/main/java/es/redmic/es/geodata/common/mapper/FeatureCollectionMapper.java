package es.redmic.es.geodata.common.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.models.es.geojson.common.model.GeoHitsWrapper;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class FeatureCollectionMapper extends CustomMapper<GeoHitsWrapper, GeoJSONFeatureCollectionDTO> {

	@SuppressWarnings("unchecked")
	@Override
	public void mapAtoB(GeoHitsWrapper a, GeoJSONFeatureCollectionDTO b, MappingContext context) {

		Class<?> targetTypeDto = (Class<?>) context.getProperty("targetTypeDto");

		if (targetTypeDto == null)
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);

		List<?> x = mapperFacade.mapAsList(a.getHits(), targetTypeDto, context);
		b.setFeatures(x);
		b.get_meta().setMax_score(a.getMax_score());
		b.setTotal(a.getTotal());
	}
}