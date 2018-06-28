package es.redmic.es.geodata.common.service;

import es.redmic.models.es.geojson.common.dto.MetaFeatureDTO;
import es.redmic.models.es.geojson.common.model.Feature;

public interface IRWGeoDataESService<TDTO extends MetaFeatureDTO<?, ?>, TModel extends Feature<?, ?>> {

	public TModel save(TModel modelToIndex);

	public TModel update(TModel modelToIndex);

	public void delete(String id, String parentId);
}
