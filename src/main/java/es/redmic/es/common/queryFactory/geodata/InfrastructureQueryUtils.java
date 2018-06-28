package es.redmic.es.common.queryFactory.geodata;

import java.util.HashSet;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;

import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

public abstract class InfrastructureQueryUtils extends DataQueryUtils {

	// @FORMATTER:OFF

	public final static BoolQueryBuilder INTERNAL_QUERY = QueryBuilders.boolQuery()
			.must(QueryBuilders.termQuery(SITE_PATH + ".id", DataPrefixType.INFRASTRUCTURE));

	protected final static String CHILDREN_NAME = "attributeseries";

	// @FORMATTER:ON

	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = getOrInitializeBaseQuery(getGeoDataQuery(queryDTO, internalQuery, partialQuery)),
				queryOnChildren = getQueryOnChildren(queryDTO);

		addMustTermIfExist(query, getZQuery(SITE_PATH, Z_PROPERTY, SEARCH_BY_Z_RANGE_SCRIPT, queryDTO.getZ()));
		addMustTermIfExist(query, getDateLimitsQuery(queryDTO.getDateLimits(), SITE_PATH + "." + DATE_PROPERTY));

		query.should(JoinQueryBuilders
				.hasChildQuery(CHILDREN_NAME,
						queryOnChildren.hasClauses() ? queryOnChildren : QueryBuilders.matchAllQuery(), ScoreMode.Avg)
				.innerHit(new InnerHitBuilder()));

		return getResultQuery(query);
	}

	private static BoolQueryBuilder getQueryOnChildren(DataQueryDTO queryDTO) {

		return QueryBuilders.boolQuery();
	}

	@SuppressWarnings("serial")
	public static HashSet<String> getFieldsExcludedOnQuery() {

		return new HashSet<String>() {
			{
				add(VALUE_QUERY_FIELD);
				add(QFLAG_QUERY_FIELD);
				add(VFLAG_QUERY_FIELD);
			}
		};
	}
}