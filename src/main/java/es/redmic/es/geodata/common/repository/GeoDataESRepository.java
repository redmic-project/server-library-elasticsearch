package es.redmic.es.geodata.common.repository;

import es.redmic.models.es.geojson.common.model.Feature;

public abstract class GeoDataESRepository<TModel extends Feature<?, ?>>
		extends RWGeoDataESRepository<TModel> {

	protected static String[] INDEX = { "activity" };
	protected static String[] TYPE = { "geodata" };

	public GeoDataESRepository() {
		super(INDEX, TYPE);
	}
}