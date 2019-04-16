package es.redmic.es.geodata.geofixedstation.repository;

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

import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.queryFactory.geodata.GeoFixedTimeSeriesQueryUtils;
import es.redmic.models.es.geojson.common.model.GeoPointData;

@Repository
public class GeoFixedTimeSeriesESRepository extends GeoFixedBaseESRepository<GeoPointData> {

	public GeoFixedTimeSeriesESRepository() {
		super();
		setInternalQuery(GeoFixedTimeSeriesQueryUtils.INTERNAL_QUERY);
	}

	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		String dashboardProperty = "properties.site.dashboard";
		if (terms.containsKey(dashboardProperty) && ((Boolean) terms.get(dashboardProperty)).equals(true)) {
			query.must(QueryBuilders.existsQuery(dashboardProperty));
		}

		return super.getTermQuery(terms, query);
	}
}
