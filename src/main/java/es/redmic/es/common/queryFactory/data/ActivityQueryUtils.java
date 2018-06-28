package es.redmic.es.common.queryFactory.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;

import es.redmic.es.common.queryFactory.geodata.DataQueryUtils;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.DateLimitsDTO;
import es.redmic.models.es.common.query.dto.ZRangeDTO;

public abstract class ActivityQueryUtils extends DataQueryUtils {

	public final static String CHILDREN_NAME = "geodata";

	@SuppressWarnings("serial")
	public final static List<String> GRANDCHILD_NAMES = new ArrayList<String>() {
		{
			add("timeseries");
			add("collecting");
		}
	};

	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = getOrInitializeBaseQuery(getBaseQuery(queryDTO, internalQuery, partialQuery)),
				queryOnChildren = getQueryOnChildren(queryDTO);

		if (queryOnChildren.hasClauses()) {
			query.must(JoinQueryBuilders.hasChildQuery(CHILDREN_NAME, queryOnChildren, ScoreMode.Avg));
		}

		if (queryDTO.getAccessibilityIds() != null && queryDTO.getAccessibilityIds().size() > 0)
			query.must(getAccessibilityQuery(queryDTO.getAccessibilityIds()));

		return getResultQuery(query);
	}

	private static BoolQueryBuilder getQueryOnChildren(DataQueryDTO queryDTO) {

		BoolQueryBuilder queryOnChildren = QueryBuilders.boolQuery();

		// Queries en series (múltiple lugares)

		BoolQueryBuilder queryOnGrandchild = QueryBuilders.boolQuery();

		addMustTermIfExist(queryOnGrandchild, getDateLimitsQuery(queryDTO.getDateLimits(), DATE_PROPERTY));
		addMustTermIfExist(queryOnGrandchild, getFlagQuery(queryDTO.getQFlags(), QFLAG_PROPERTY));
		addMustTermIfExist(queryOnGrandchild, getFlagQuery(queryDTO.getVFlags(), VFLAG_PROPERTY));
		addMustTermIfExist(queryOnGrandchild, getZQuery(Z_PROPERTY, SEARCH_BY_Z_RANGE_SCRIPT, queryDTO.getZ()));

		// Queries en geodata (múltiples lugares)

		addMustTermIfExist(queryOnChildren, getDateQueryOnChildren(queryDTO.getDateLimits(), queryOnGrandchild));
		addMustTermIfExist(queryOnChildren, getQFlagsQueryOnChildren(queryDTO.getQFlags(), queryOnGrandchild));
		addMustTermIfExist(queryOnChildren, getVFlagsQueryOnChildren(queryDTO.getVFlags(), queryOnGrandchild));
		addMustTermIfExist(queryOnChildren, getZRangeQueryOnChildren(queryDTO.getZ(), queryOnGrandchild));

		// Queries específicas en geodata
		addMustTermIfExist(queryOnChildren, getBBoxQuery(queryDTO.getBbox()));
		// Queries específicas en series
		addMustTermIfExist(queryOnChildren, getQueryOnGrandchild(queryDTO));

		return queryOnChildren;
	}

	/*
	 * Realiza búsquedas de fechas en todas las localizaciones posibles
	 * 
	 */

	private static BoolQueryBuilder getDateQueryOnChildren(DateLimitsDTO dateTime, BoolQueryBuilder queryOnGrandchild) {

		if (dateTime == null)
			return null;

		String collectStartDate = COLLECT_PATH + "." + START_DATE_PROPERTY,
				collectEndDate = COLLECT_PATH + "." + END_DATE_PROPERTY,
				inTrackDate = INTRACK_PATH + "." + DATE_PROPERTY, siteDate = SITE_PATH + "." + DATE_PROPERTY;

		BoolQueryBuilder queryOnChildren = QueryBuilders.boolQuery()
				.should(getDateLimitsQuery(dateTime, collectStartDate, collectEndDate))
				.should(getDateLimitsQuery(dateTime, inTrackDate)).should(getDateLimitsQuery(dateTime, siteDate));

		addQueryToGrandChild(queryOnChildren, queryOnGrandchild);

		return queryOnChildren;
	}

	/*
	 * Realiza búsquedas de qFlag en todas las localizaciones posibles
	 * 
	 */

	private static BoolQueryBuilder getQFlagsQueryOnChildren(List<String> qFlags,
			BoolQueryBuilder queryOnGrandchild) {

		if (qFlags == null || qFlags.size() < 0)
			return null;

		BoolQueryBuilder queryOnChildren = QueryBuilders.boolQuery()
				.should(getFlagQuery(qFlags, INTRACK_PATH + "." + QFLAG_PROPERTY))
				.should(getFlagQuery(qFlags, COLLECT_PATH + "." + QFLAG_PROPERTY));

		addQueryToGrandChild(queryOnChildren, queryOnGrandchild);

		return queryOnChildren;
	}

	/*
	 * Realiza búsquedas de vFlag en todas las localizaciones posibles
	 * 
	 */

	private static BoolQueryBuilder getVFlagsQueryOnChildren(List<String> vFlags,
			BoolQueryBuilder queryOnGrandchild) {

		if (vFlags == null || vFlags.size() < 1)
			return null;

		BoolQueryBuilder queryOnChildren = QueryBuilders.boolQuery()
				.should(getFlagQuery(vFlags, INTRACK_PATH + "." + VFLAG_PROPERTY))
				.should(getFlagQuery(vFlags, COLLECT_PATH + "." + VFLAG_PROPERTY));

		addQueryToGrandChild(queryOnChildren, queryOnGrandchild);

		return queryOnChildren;
	}

	/*
	 * Realiza búsquedas de vFlag en todas las localizaciones posibles
	 * 
	 */

	private static BoolQueryBuilder getZRangeQueryOnChildren(ZRangeDTO zRangeDTO, BoolQueryBuilder queryOnGrandchild) {

		if (zRangeDTO == null)
			return null;

		BoolQueryBuilder queryOnChildren = QueryBuilders.boolQuery()
				.should(getZQuery(INTRACK_PATH, Z_PROPERTY, SEARCH_BY_Z_RANGE_SCRIPT, zRangeDTO))
				.should(getZQuery(COLLECT_PATH, Z_PROPERTY, SEARCH_BY_Z_RANGE_SCRIPT, zRangeDTO))
				.should(getZQuery(SITE_PATH, Z_PROPERTY, SEARCH_BY_Z_RANGE_SCRIPT, zRangeDTO))
				.should(getZNestedQuery(MEASUREMENT_PATH, DATA_DEFINITION_PROPERTY, Z_PROPERTY,
						SEARCH_NESTED_BY_Z_RANGE_SCRIPT, zRangeDTO));

		addQueryToGrandChild(queryOnChildren, queryOnGrandchild);

		return queryOnChildren;
	}

	/*
	 * Realiza búsquedas simples (solo una posible localización) sobre series
	 * 
	 */

	private static BoolQueryBuilder getQueryOnGrandchild(DataQueryDTO queryDTO) {

		BoolQueryBuilder queryOnGrandchild = QueryBuilders.boolQuery(), baseQuery = QueryBuilders.boolQuery();

		addMustTermIfExist(queryOnGrandchild, getValueQuery(queryDTO.getValue(), VALUE_PROPERTY));

		addQueryToGrandChild(baseQuery, queryOnGrandchild);

		return baseQuery.hasClauses() ? baseQuery : null;
	}

	/*
	 * Añade términos de query sobre las series (en caso de existir) a nivel de
	 * geodata
	 * 
	 */

	private static void addQueryToGrandChild(BoolQueryBuilder queryOnChildren, BoolQueryBuilder queryOnGrandchild) {

		if (queryOnGrandchild.hasClauses()) {
			for (String grandchildName : GRANDCHILD_NAMES) {
				queryOnChildren
						.should(JoinQueryBuilders.hasChildQuery(grandchildName, queryOnGrandchild, ScoreMode.Avg));
			}
		}
	}

	@SuppressWarnings("serial")
	public static BoolQueryBuilder getItemsQuery(String id, List<Long> accessibilityIds) {

		ArrayList<String> ids = new ArrayList<String>() {
			{
				add(id);
			}
		};
		return getItemsQuery(ids, accessibilityIds);
	}

	public static BoolQueryBuilder getItemsQuery(List<String> ids, List<Long> accessibilityIds) {

		BoolQueryBuilder query = QueryBuilders.boolQuery();
		if (accessibilityIds != null && accessibilityIds.size() > 0)
			query.must(getAccessibilityQuery(accessibilityIds));

		query.must(QueryBuilders.idsQuery().addIds(ids.toArray(new String[ids.size()])));
		return query;
	}
}