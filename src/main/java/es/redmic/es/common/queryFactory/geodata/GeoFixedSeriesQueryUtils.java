package es.redmic.es.common.queryFactory.geodata;

import java.util.HashSet;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;

import es.redmic.models.es.common.query.dto.DataQueryDTO;

public abstract class GeoFixedSeriesQueryUtils extends DataQueryUtils {

	protected static BoolQueryBuilder getGeoFixedSeriesQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery, String childrenName) {
		BoolQueryBuilder query = getGeoDataQuery(queryDTO, internalQuery, partialQuery),
				queryOnChildren = getQueryOnChildren(queryDTO);

		addMustTermIfExist(query, getZNestedQuery(MEASUREMENT_PATH, DATA_DEFINITION_PROPERTY, Z_PROPERTY,
				SEARCH_NESTED_BY_Z_RANGE_SCRIPT, queryDTO.getZ()));

		if (queryOnChildren.hasClauses())
			query.must(JoinQueryBuilders.hasChildQuery(childrenName, queryOnChildren, ScoreMode.Avg));

		return getResultQuery(query);
	}

	private static BoolQueryBuilder getQueryOnChildren(DataQueryDTO queryDTO) {

		BoolQueryBuilder queryOnChildren = QueryBuilders.boolQuery();

		addMustTermIfExist(queryOnChildren, getFlagQuery(queryDTO.getQFlags(), QFLAG_PROPERTY));
		addMustTermIfExist(queryOnChildren, getFlagQuery(queryDTO.getVFlags(), VFLAG_PROPERTY));
		addMustTermIfExist(queryOnChildren, getValueQuery(queryDTO.getValue(), VALUE_PROPERTY));
		addMustTermIfExist(queryOnChildren, getDateLimitsQuery(queryDTO.getDateLimits(), DATE_PROPERTY));

		return queryOnChildren;
	}

	@SuppressWarnings("serial")
	public static HashSet<String> getFieldsExcludedOnQuery() {

		return new HashSet<String>() {
			{
				add(PRECISION_QUERY_FIELD);
			}
		};
	}
}
