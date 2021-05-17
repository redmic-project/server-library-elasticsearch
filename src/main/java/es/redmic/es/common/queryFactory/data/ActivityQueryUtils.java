package es.redmic.es.common.queryFactory.data;

/*-
 * #%L
 * ElasticSearch
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.es.common.queryFactory.common.BaseQueryUtils;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.DateLimitsDTO;

public abstract class ActivityQueryUtils extends BaseQueryUtils {

	public static final String START_DATE_FIELD = "startDate";
	public static final String END_DATE_FIELD = "endDate";
	public static final String GEOMETRY_PROPERTY = "spatialExtension";

	protected ActivityQueryUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = getOrInitializeBaseQuery(getBaseQuery(queryDTO, internalQuery, partialQuery));

		if (queryDTO.getAccessibilityIds() != null && !queryDTO.getAccessibilityIds().isEmpty())
			query.must(getAccessibilityQuery(queryDTO.getAccessibilityIds()));

		if (queryDTO.getDateLimits() != null) {
			query.must(getMetadataDateLimitsQuery(queryDTO.getDateLimits()));
		}

		if (queryDTO.getBbox() != null) {
			query.must(getBBoxQuery(queryDTO.getBbox(), GEOMETRY_PROPERTY));
		}

		if (queryDTO.getStarred() != null) {
			query.must(QueryBuilders.termsQuery("starred", queryDTO.getStarred()));
		}

		if (queryDTO.getResources() != null) {
			query.must(QueryBuilders.nestedQuery("resources", QueryBuilders.existsQuery("resources.name"), ScoreMode.Avg));
		}

		if (queryDTO.getResourceName() != null) {
			query.must(QueryBuilders.nestedQuery("resources", QueryBuilders.termQuery("resources.name",
				queryDTO.getResourceName()), ScoreMode.Avg));
		}

		return getResultQuery(query);
	}

	protected static QueryBuilder getMetadataDateLimitsQuery(DateLimitsDTO dateLimitsDTO) {

		if (dateLimitsDTO == null)
			return null;

		BoolQueryBuilder metadataDateLimitsQuery = QueryBuilders.boolQuery();

		if (dateLimitsDTO.getStartDate() != null)
			metadataDateLimitsQuery.must(QueryBuilders.rangeQuery(START_DATE_FIELD).gte(dateLimitsDTO.getStartDate()));
		if (dateLimitsDTO.getEndDate() != null)
			metadataDateLimitsQuery.must(QueryBuilders.rangeQuery(END_DATE_FIELD).lte(dateLimitsDTO.getEndDate()));

		return metadataDateLimitsQuery;
	}



	public static BoolQueryBuilder getItemsQuery(String id, List<Long> accessibilityIds) {

		ArrayList<String> ids = new ArrayList<>();
		ids.add(id);
		return getItemsQuery(ids, accessibilityIds);
	}

	public static BoolQueryBuilder getItemsQuery(List<String> ids, List<Long> accessibilityIds) {

		BoolQueryBuilder query = QueryBuilders.boolQuery();
		if (accessibilityIds != null && !accessibilityIds.isEmpty())
			query.must(getAccessibilityQuery(accessibilityIds));

		query.must(QueryBuilders.idsQuery().addIds(ids.toArray(new String[ids.size()])));
		return query;
	}
}
