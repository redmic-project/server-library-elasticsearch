package es.redmic.es.geodata.common.service;

import java.util.List;

import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.models.es.geojson.common.dto.MetaFeatureDTO;
import es.redmic.models.es.geojson.common.model.Feature;

public interface IRGeoDataESService<TDTO extends MetaFeatureDTO<?,?>, TModel extends Feature<?, ?>> {

	public TDTO get(String id, String parentId);
	public TDTO searchById(String id);
	public GeoJSONFeatureCollectionDTO find(DataQueryDTO query, String parentId);
	public GeoJSONFeatureCollectionDTO find(DataQueryDTO query);
	public List<String> suggest(String parentId, DataQueryDTO queryDTO);
	public GeoJSONFeatureCollectionDTO mget(MgetDTO dto, String parentId);
}
