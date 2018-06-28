package es.redmic.es.common.queryFactory.geodata;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

public abstract class GeoFixedObjectCollectingSeriesQueryUtils extends GeoFixedSeriesQueryUtils {
	
	// @FORMATTER:OFF
	
	public final static String CHILDREN_NAME = "collecting";

	public final static BoolQueryBuilder INTERNAL_QUERY = 
			QueryBuilders.boolQuery().must(QueryBuilders.termQuery(SITE_PATH + ".id", DataPrefixType.OBJECT_COLLECTING));
	
	// @FORMATTER:ON
	
	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery, QueryBuilder partialQuery) {
		return getGeoFixedSeriesQuery(queryDTO, internalQuery, partialQuery, CHILDREN_NAME);
	}
}
