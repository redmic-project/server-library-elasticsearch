package es.redmic.es.geodata.common.service;


import es.redmic.es.geodata.common.repository.GeoPresenceESRepository;
import es.redmic.models.es.geojson.common.dto.MetaFeatureDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;

public abstract class GeoPresenceESService<TDTO extends MetaFeatureDTO<?, ?>, TModel extends Feature<GeoDataProperties, ?>>
		extends GeoDataESService<TDTO, TModel> {

	public GeoPresenceESService() {
	}

	public GeoPresenceESService(GeoPresenceESRepository<TModel> repository) {
		super(repository);
	}
}