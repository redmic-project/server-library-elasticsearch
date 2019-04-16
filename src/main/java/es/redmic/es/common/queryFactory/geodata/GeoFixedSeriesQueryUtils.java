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
