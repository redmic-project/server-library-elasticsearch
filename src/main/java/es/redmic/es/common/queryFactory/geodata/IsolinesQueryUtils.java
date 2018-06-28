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

public abstract class IsolinesQueryUtils extends DataQueryUtils {

	public final static BoolQueryBuilder INTERNAL_QUERY = QueryBuilders.boolQuery()
			.must(QueryBuilders.termQuery("properties.samplingPlace.id", DataPrefixType.ISOLINES));

	protected final static String CHILDREN_NAME = "timeseries";

	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = getOrInitializeBaseQuery(getGeoDataQuery(queryDTO, internalQuery, partialQuery)),
				queryOnChildren = getQueryOnChildren(queryDTO);

		query.must(JoinQueryBuilders
				.hasChildQuery(CHILDREN_NAME,
						queryOnChildren.hasClauses() ? queryOnChildren : QueryBuilders.matchAllQuery(), ScoreMode.Avg)
				.innerHit(new InnerHitBuilder()));

		return getResultQuery(query);
	}

	private static BoolQueryBuilder getQueryOnChildren(DataQueryDTO queryDTO) {

		BoolQueryBuilder queryOnChildren = QueryBuilders.boolQuery();

		addMustTermIfExist(queryOnChildren, getValueQuery(queryDTO.getValue(), VALUE_PROPERTY));
		addMustTermIfExist(queryOnChildren, getFlagQuery(queryDTO.getQFlags(), QFLAG_PROPERTY));
		addMustTermIfExist(queryOnChildren, getFlagQuery(queryDTO.getVFlags(), VFLAG_PROPERTY));

		return queryOnChildren;
	}

	@SuppressWarnings("serial")
	public static HashSet<String> getFieldsExcludedOnQuery() {

		return new HashSet<String>() {
			{
				add(DATELIMIT_QUERY_FIELD);
			}
		};
	}
}