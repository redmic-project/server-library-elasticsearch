package es.redmic.es.geodata.common.objectfactory;

import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Geometry;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.ObjectFactory;

@Component
public class GeometryESFactory<TGeometry extends Geometry> implements ObjectFactory<TGeometry> {

	@SuppressWarnings("unchecked")
	@Override
	public TGeometry create(Object source, MappingContext mappingContext) {
		return (TGeometry) source;
	}
}
