package es.redmic.es.geodata.geofixedstation.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.common.queryFactory.geodata.GeoFixedObjectCollectingSeriesQueryUtils;
import es.redmic.models.es.geojson.common.model.GeoLineStringData;

@Repository
public class GeoFixedObjectCollectingSeriesESRepository extends GeoFixedBaseESRepository<GeoLineStringData> {

	public GeoFixedObjectCollectingSeriesESRepository() {
		super();
		setInternalQuery(GeoFixedObjectCollectingSeriesQueryUtils.INTERNAL_QUERY);
	}
}