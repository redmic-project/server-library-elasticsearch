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

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;

public abstract class GeoFixedObjectCollectingSeriesQueryUtils extends GeoFixedSeriesQueryUtils {

	// @FORMATTER:OFF

	public static final String CHILDREN_NAME = "collecting";

	public static final BoolQueryBuilder INTERNAL_QUERY =
			QueryBuilders.boolQuery().must(QueryBuilders.termQuery(SITE_PATH + ".id", DataPrefixType.OBJECT_COLLECTING));

	// @FORMATTER:ON

	public static BoolQueryBuilder getQuery(GeoDataQueryDTO queryDTO, QueryBuilder internalQuery, QueryBuilder partialQuery) {
		return getGeoFixedSeriesQuery(queryDTO, internalQuery, partialQuery, CHILDREN_NAME);
	}
}
