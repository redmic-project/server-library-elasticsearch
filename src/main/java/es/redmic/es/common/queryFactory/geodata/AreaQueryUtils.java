package es.redmic.es.common.queryFactory.geodata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

public abstract class AreaQueryUtils extends DataQueryUtils {

	// @FORMATTER:OFF

	public final static BoolQueryBuilder INTERNAL_QUERY = QueryBuilders.boolQuery()
			.must(QueryBuilders.termQuery(SAMPLINGPLACE_PATH + ".id", DataPrefixType.AREA));

	// @FORMATTER:ON

	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = getOrInitializeBaseQuery(getGeoDataQuery(queryDTO, internalQuery, partialQuery));

		return getResultQuery(query);
	}

	public static HashSet<String> getFieldsExcludedOnQuery() {

		return new HashSet<String>();
	}

	public static Map<String, Object> getActivityCategoryTermQuery() {

		Map<String, Object> terms = new HashMap<>();
		terms.put("activityCategory", DataPrefixType.AREA);
		return terms;
	}
}