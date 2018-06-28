package es.redmic.es.common.queryFactory.geodata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

public abstract class CitationQueryUtils extends DataQueryUtils {

	// @FORMATTER:OFF

	public final static BoolQueryBuilder INTERNAL_QUERY = 
			QueryBuilders.boolQuery().must(QueryBuilders.termQuery(COLLECT_PATH + ".id", DataPrefixType.CITATION));
	
	// @FORMATTER:ON
	
	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = getOrInitializeBaseQuery(getGeoDataQuery(queryDTO, internalQuery, partialQuery));

		addMustTermIfExist(query, getZQuery(COLLECT_PATH, Z_PROPERTY, SEARCH_BY_Z_RANGE_SCRIPT, queryDTO.getZ()));
	
		String startDateInCollect = COLLECT_PATH + "." + START_DATE_PROPERTY,
				endDateInCollect = COLLECT_PATH + "." + END_DATE_PROPERTY;
		// TODO: no tiene en cuenta la intesección en los límites. Hacer con script similar a z.
		addMustTermIfExist(query, getDateLimitsQuery(queryDTO.getDateLimits(), startDateInCollect, endDateInCollect));

		addMustTermIfExist(query, getPrecisionQuery(queryDTO.getPrecision()));
		
		return getResultQuery(query);
	}

	@SuppressWarnings("serial")
	public static HashSet<String> getFieldsExcludedOnQuery() {

		return new HashSet<String>() {
			{
				add(QFLAG_QUERY_FIELD);
				add(VFLAG_QUERY_FIELD);
			}
		};
	}
	
	public static Map<String, Object> getActivityCategoryTermQuery() {
		
		Map<String, Object> terms = new HashMap<>();
		terms.put("activityCategory", DataPrefixType.CITATION);
		return terms;
	}
}