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
import java.util.Set;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;

import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;

public abstract class IsolinesQueryUtils extends GeoDataQueryUtils {

	public static final BoolQueryBuilder INTERNAL_QUERY = QueryBuilders.boolQuery()
			.must(QueryBuilders.termQuery("properties.samplingPlace.id", DataPrefixType.ISOLINES));

	protected static final String CHILDREN_NAME = "timeseries";

	public static BoolQueryBuilder getQuery(GeoDataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = getOrInitializeBaseQuery(getGeoDataQuery(queryDTO, internalQuery, partialQuery));
		BoolQueryBuilder queryOnChildren = getQueryOnChildren(queryDTO);

		query.must(JoinQueryBuilders
				.hasChildQuery(CHILDREN_NAME,
						queryOnChildren.hasClauses() ? queryOnChildren : QueryBuilders.matchAllQuery(), ScoreMode.Avg)
				.innerHit(new InnerHitBuilder()));

		return getResultQuery(query);
	}

	private static BoolQueryBuilder getQueryOnChildren(GeoDataQueryDTO queryDTO) {

		BoolQueryBuilder queryOnChildren = QueryBuilders.boolQuery();

		addMustTermIfExist(queryOnChildren, getValueQuery(queryDTO.getValue(), VALUE_PROPERTY));
		addMustTermIfExist(queryOnChildren, getFlagQuery(queryDTO.getQFlags(), QFLAG_PROPERTY));
		addMustTermIfExist(queryOnChildren, getFlagQuery(queryDTO.getVFlags(), VFLAG_PROPERTY));

		return queryOnChildren;
	}

	public static Set<String> getFieldsExcludedOnQuery() {

		HashSet<String> fieldsExcludedOnQuery = new HashSet<>();
		fieldsExcludedOnQuery.add(DATELIMIT_QUERY_FIELD);
		return fieldsExcludedOnQuery;
	}
}
