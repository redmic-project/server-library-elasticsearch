package es.redmic.test.utils;

import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;

public class GeoFeatureWrapperConfig extends ConfigMapper {
	
	public GeoFeatureWrapperConfig() {
		this.setInClass(GeoSearchWrapper.class);
	}
}
