package es.redmic.es.geodata.geofixedstation.mapper;

import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.geojson.common.dto.FixedSurveySeriesPropertiesDTO;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class FixedSurveySeriesPropertiesESMapper
		extends CustomMapper<GeoDataProperties, FixedSurveySeriesPropertiesDTO> {

	@Override
	public void mapAtoB(GeoDataProperties a, FixedSurveySeriesPropertiesDTO b, MappingContext context) {

		String geoDataPrefix = (String) context.getProperty("geoDataPrefix");

		if (geoDataPrefix == null)
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
	}

	@Override
	public void mapBtoA(FixedSurveySeriesPropertiesDTO b, GeoDataProperties a, MappingContext context) {

		String geoDataPrefix = (String) context.getProperty("geoDataPrefix");

		if (geoDataPrefix == null)
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);

		a.getSite().setId(DataMapperUtils.convertIdentifier(b.getSite().getId(), geoDataPrefix));
	}
}
