package es.redmic.es.geodata.geofixedstation.repository;

import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.queryFactory.geodata.GeoFixedTimeSeriesQueryUtils;
import es.redmic.models.es.geojson.common.model.GeoPointData;

@Repository
public class GeoFixedTimeSeriesESRepository extends GeoFixedBaseESRepository<GeoPointData> {

	public GeoFixedTimeSeriesESRepository() {
		super();
		setInternalQuery(GeoFixedTimeSeriesQueryUtils.INTERNAL_QUERY);
	}

	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		String dashboardProperty = "properties.site.dashboard";
		if (terms.containsKey(dashboardProperty) && ((Boolean) terms.get(dashboardProperty)).equals(true)) {
			query.must(QueryBuilders.existsQuery(dashboardProperty));
		}

		return super.getTermQuery(terms, query);
	}
}