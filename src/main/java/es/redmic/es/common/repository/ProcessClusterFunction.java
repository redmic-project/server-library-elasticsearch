package es.redmic.es.common.repository;

import java.util.List;

import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Point;

import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.tracking.common.TrackingClustersDTO;


public class ProcessClusterFunction implements IProcessItemFunction<GeoHitWrapper<GeoDataProperties, Point>> {
	
	TrackingClustersDTO items;
	int zoomLevel;
	
	protected ObjectMapper objectMapper;
	
	
	public ProcessClusterFunction(ObjectMapper objectMapper, int zoomLevel) {
		this.objectMapper = objectMapper;
		this.zoomLevel = zoomLevel;
		items = new TrackingClustersDTO(zoomLevel);
	}

	@Override
	public void process(SearchHit hit) {
		GeoHitWrapper<GeoDataProperties, Point> item = mapper(hit);
		items.addFeature(item);
	}
	
	private GeoHitWrapper<GeoDataProperties, Point> mapper(SearchHit hit) {
		GeoPointData data = objectMapper.convertValue(hit.getSource(), GeoPointData.class);
		GeoHitWrapper<GeoDataProperties, Point> item = new GeoHitWrapper<GeoDataProperties, Point>();
		item.set_source(data);
		item.set_id(hit.getId());
		item.set_version(hit.getVersion());
		
		return item;
	}

	@Override
	public List<?> getResults() {
		return items;
	}

}
