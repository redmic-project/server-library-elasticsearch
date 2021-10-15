package es.redmic.es.common.queryFactory.geodata;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;

public abstract class TrackingQueryUtils extends GeoDataQueryUtils {

	// @FORMATTER:OFF

	public static final BoolQueryBuilder INTERNAL_QUERY = QueryBuilders.boolQuery()
			.must(QueryBuilders.boolQuery()
					.should(QueryBuilders.termQuery(INTRACK_PATH + ".id", DataPrefixType.PLATFORM_TRACKING))
					.should(QueryBuilders.termQuery(INTRACK_PATH + ".id", DataPrefixType.ANIMAL_TRACKING)));

	// @FORMATTER:ON

	public static BoolQueryBuilder getQuery(GeoDataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {
		return getTrackingQuery(queryDTO, internalQuery, partialQuery);
	}

	protected static BoolQueryBuilder getTrackingQuery(GeoDataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = getGeoDataQuery(queryDTO, internalQuery, partialQuery);

		addMustTermIfExist(query, getZQuery(INTRACK_PATH, Z_PROPERTY, queryDTO.getZ()));
		addMustTermIfExist(query, getDateLimitsQuery(queryDTO.getDateLimits(), INTRACK_PATH + "." + DATE_PROPERTY));
		addMustTermIfExist(query, getFlagQuery(queryDTO.getQFlags(), INTRACK_PATH + "." + QFLAG_PROPERTY));
		addMustTermIfExist(query, getFlagQuery(queryDTO.getVFlags(), INTRACK_PATH + "." + VFLAG_PROPERTY));
		addMustTermIfExist(query, getPrecisionQuery(queryDTO.getPrecision()));

		return getResultQuery(query);
	}

	public static Set<String> getFieldsExcludedOnQuery() {

		HashSet<String> fieldsExcludedOnQuery = new HashSet<>();
		fieldsExcludedOnQuery.add(VALUE_QUERY_FIELD);
		return fieldsExcludedOnQuery;
	}

	public static Map<String, Object> getActivityCategoryTermQuery() {

		Map<String, Object> terms = new HashMap<>();
		terms.put("activityCategory", DataPrefixType.TRACKING + "," + DataPrefixType.ANIMAL_TRACKING + ","
				+ DataPrefixType.PLATFORM_TRACKING);
		return terms;
	}
}
