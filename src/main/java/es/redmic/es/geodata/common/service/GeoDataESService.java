package es.redmic.es.geodata.common.service;


import es.redmic.es.geodata.common.repository.GeoDataESRepository;
import es.redmic.models.es.geojson.common.dto.MetaFeatureDTO;
import es.redmic.models.es.geojson.common.model.Feature;

public abstract class GeoDataESService<TDTO extends MetaFeatureDTO<?, ?>, TModel extends Feature<?, ?>>
		extends RWGeoDataESService<TDTO, TModel> {

	public GeoDataESService() {
	}

	public GeoDataESService(GeoDataESRepository<TModel> repository) {
		super(repository);
	}
}