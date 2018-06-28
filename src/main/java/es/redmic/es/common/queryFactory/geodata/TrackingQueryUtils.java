package es.redmic.es.common.queryFactory.geodata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

public abstract class TrackingQueryUtils extends DataQueryUtils {

	// @FORMATTER:OFF

	public final static BoolQueryBuilder INTERNAL_QUERY = QueryBuilders.boolQuery()
			.must(QueryBuilders.boolQuery()
					.should(QueryBuilders.termQuery(INTRACK_PATH + ".id", DataPrefixType.PLATFORM_TRACKING))
					.should(QueryBuilders.termQuery(INTRACK_PATH + ".id", DataPrefixType.ANIMAL_TRACKING)));

	// @FORMATTER:ON

	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {
		return getTrackingQuery(queryDTO, internalQuery, partialQuery);
	}

	protected static BoolQueryBuilder getTrackingQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = getGeoDataQuery(queryDTO, internalQuery, partialQuery);

		addMustTermIfExist(query, getZQuery(INTRACK_PATH, Z_PROPERTY, SEARCH_BY_Z_RANGE_SCRIPT, queryDTO.getZ()));
		addMustTermIfExist(query, getDateLimitsQuery(queryDTO.getDateLimits(), INTRACK_PATH + "." + DATE_PROPERTY));
		addMustTermIfExist(query, getFlagQuery(queryDTO.getQFlags(), INTRACK_PATH + "." + QFLAG_PROPERTY));
		addMustTermIfExist(query, getFlagQuery(queryDTO.getVFlags(), INTRACK_PATH + "." + VFLAG_PROPERTY));
		addMustTermIfExist(query, getPrecisionQuery(queryDTO.getPrecision()));

		return getResultQuery(query);
	}

	@SuppressWarnings("serial")
	public static HashSet<String> getFieldsExcludedOnQuery() {

		return new HashSet<String>() {
			{
				add(VALUE_QUERY_FIELD);
			}
		};
	}

	public static Map<String, Object> getActivityCategoryTermQuery() {

		Map<String, Object> terms = new HashMap<>();
		terms.put("activityCategory", DataPrefixType.TRACKING + "," + DataPrefixType.ANIMAL_TRACKING + ","
				+ DataPrefixType.PLATFORM_TRACKING);
		return terms;
	}
}
