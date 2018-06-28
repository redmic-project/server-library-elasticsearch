package es.redmic.es.common.queryFactory.geodata;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

public abstract class AnimalTrackingQueryUtils extends TrackingQueryUtils {

	// @FORMATTER:OFF

	public final static BoolQueryBuilder INTERNAL_QUERY = QueryBuilders.boolQuery()
			.must(QueryBuilders.termQuery(INTRACK_PATH + ".id", DataPrefixType.ANIMAL_TRACKING));

	// @FORMATTER:ON

	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {
		return getTrackingQuery(queryDTO, internalQuery, partialQuery);
	}
}