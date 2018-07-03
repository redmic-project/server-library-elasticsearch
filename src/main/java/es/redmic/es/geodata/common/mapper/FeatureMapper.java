package es.redmic.es.geodata.common.mapper;

import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.geojson.common.dto.MetaFeatureDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.utils.geo.performance.Simplify;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class FeatureMapper extends CustomMapper<GeoHitWrapper, MetaFeatureDTO> {

	@SuppressWarnings("unchecked")
	@Override
	public void mapAtoB(GeoHitWrapper a, MetaFeatureDTO b, MappingContext context) {

		Class<?> targetTypeDto = (Class<?>) context.getProperty("targetTypeDto");

		if (targetTypeDto == null)
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);

		context.setProperty("inner_hits", a.getInner_hits());
		
		Feature source = a.get_source();

		MetaFeatureDTO featureDTO = (MetaFeatureDTO) mapperFacade.map(source, targetTypeDto, context);
		b.setProperties(featureDTO.getProperties());

		Geometry geom = a.get_source().getGeometry();

		if (geom != null) {

			// TODO Leer el zoom desde el contexto
			if (!Point.class.isInstance(geom) && !MultiPolygon.class.isInstance(geom))
				b.setGeometry(Simplify.simplify(geom, 9));
			else
				b.setGeometry(geom);
		}

		b.setId(featureDTO.getId());
		b.setUuid(featureDTO.getUuid());

		if (featureDTO.getProperties() != null) {
			b.getProperties().setActivityId(a.get_parent());
		}

		b.get_meta().setScore(a.get_score());
		b.get_meta().setVersion(a.get_version());
		b.get_meta().setHighlight(a.getHighlight());
	}
}