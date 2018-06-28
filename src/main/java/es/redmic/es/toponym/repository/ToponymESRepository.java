package es.redmic.es.toponym.repository;

import java.util.HashMap;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JavaType;
import com.vividsolutions.jts.geom.Geometry;

import es.redmic.es.geodata.common.repository.RWGeoDataESRepository;
import es.redmic.models.es.common.request.dto.CategoryPathInfo;
import es.redmic.models.es.geojson.toponym.model.Toponym;
import es.redmic.models.es.geojson.toponym.model.ToponymProperties;

@Repository
public class ToponymESRepository extends RWGeoDataESRepository<Toponym>{

	protected static String[] INDEX = { "toponym" };
	protected static String[] TYPE = { "toponym" };

	public ToponymESRepository() {
		super(INDEX, TYPE);
	}
	
	@Override
	protected JavaType getSourceType(Class<?> wrapperClass) {
		return objectMapper.getTypeFactory().constructParametricType(wrapperClass, ToponymProperties.class, Geometry.class);
	}
	
	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "properties.name", "properties.name.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "properties.name", "properties.name.suggest" };
	}
	
	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "properties.name" };
	}
	
	@Override
	public HashMap<String, CategoryPathInfo> getCategoriesPaths() {
		return null;
	}
}