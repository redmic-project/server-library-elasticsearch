package es.redmic.es.geodata.common.repository;

import java.util.List;

import es.redmic.es.common.repository.IRBaseESRepository;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.models.es.geojson.common.model.GeoHitsWrapper;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;

public interface IRGeoDataESRepository<TModel extends Feature<?, ?>> extends IRBaseESRepository<TModel> {

	public GeoHitWrapper<?, ?> findById(String id, String parentId);
	public GeoSearchWrapper<?, ?> searchByIds(String[] ids);
	public List<String> suggest(String parentId, DataQueryDTO queryDTO);
	public GeoHitsWrapper<?, ?> mget(MgetDTO dto, String parentId);
	public GeoSearchWrapper<?, ?> find(DataQueryDTO queryDTO, String parentId);
	public GeoSearchWrapper<?, ?> find(DataQueryDTO queryDTO);
	public GeoHitWrapper<?, ?> findById(String id);
	public GeoHitsWrapper<?, ?> mget(MgetDTO dto);
}