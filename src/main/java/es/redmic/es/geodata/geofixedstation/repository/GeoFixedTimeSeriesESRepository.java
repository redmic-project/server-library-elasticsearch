package es.redmic.es.geodata.geofixedstation.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.common.queryFactory.geodata.GeoFixedTimeSeriesQueryUtils;
import es.redmic.models.es.geojson.common.model.GeoPointData;

@Repository
public class GeoFixedTimeSeriesESRepository extends GeoFixedBaseESRepository<GeoPointData> {
	
	public GeoFixedTimeSeriesESRepository() {
		super();
		setInternalQuery(GeoFixedTimeSeriesQueryUtils.INTERNAL_QUERY);
	}
}