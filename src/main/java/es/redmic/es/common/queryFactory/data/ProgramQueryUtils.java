package es.redmic.es.common.queryFactory.data;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import es.redmic.es.common.queryFactory.geodata.DataQueryUtils;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

public abstract class ProgramQueryUtils extends DataQueryUtils {

	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = getOrInitializeBaseQuery(getBaseQuery(queryDTO, internalQuery, partialQuery));

		if (queryDTO.getAccessibilityIds() != null && queryDTO.getAccessibilityIds().size() > 0)
			query.must(getAccessibilityQuery(queryDTO.getAccessibilityIds()));

		return getResultQuery(query);
	}
}