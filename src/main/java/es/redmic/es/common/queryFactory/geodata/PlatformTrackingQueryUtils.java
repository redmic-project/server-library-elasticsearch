package es.redmic.es.common.queryFactory.geodata;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

public abstract class PlatformTrackingQueryUtils extends TrackingQueryUtils {

	// @FORMATTER:OFF

	public final static BoolQueryBuilder INTERNAL_QUERY = QueryBuilders.boolQuery()
			.must(QueryBuilders.termQuery(INTRACK_PATH + ".id", DataPrefixType.PLATFORM_TRACKING));

	// @FORMATTER:ON

	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {
		return getTrackingQuery(queryDTO, internalQuery, partialQuery);
	}

	public static Map<String, Object> getActivityCategoryTermQuery() {

		Map<String, Object> terms = new HashMap<>();
		terms.put("activityCategory", DataPrefixType.INFRASTRUCTURE);
		return terms;
	}
}