package es.redmic.test.unit.queryFactory.geodata;

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

import java.io.IOException;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import es.redmic.es.common.queryFactory.geodata.GeoFixedTimeSeriesQueryUtils;
import es.redmic.test.unit.queryFactory.common.GeoDataQueryTest;

@RunWith(MockitoJUnitRunner.class)
public class GeoFixedTimeSeriesQueryTest extends GeoDataQueryTest {

	protected String parentId = "239";

	@Test
	public void getQuery_ReturnInternalQuery_IfIsDefaultQueryDTO() throws IOException, JSONException {

		BoolQueryBuilder query = GeoFixedTimeSeriesQueryUtils.getQuery(geoDataQueryDTO,
				GeoFixedTimeSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedTimeSeriesQueryUtils.getHierarchicalQuery(geoDataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/geofixedtimeseries/internalQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnDateLimitsQueryOnChildren_IfQueryDTOHasDateLimisQueryDTO()
			throws IOException, JSONException {

		createDateLimitsQuery();

		BoolQueryBuilder query = GeoFixedTimeSeriesQueryUtils.getQuery(geoDataQueryDTO,
				GeoFixedTimeSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedTimeSeriesQueryUtils.getHierarchicalQuery(geoDataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/geofixedtimeseries/dateLimitsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnValueQueryOnChildren_IfQueryDTOHasValueQueryDTO() throws IOException, JSONException {

		createValueQuery();

		BoolQueryBuilder query = GeoFixedTimeSeriesQueryUtils.getQuery(geoDataQueryDTO,
				GeoFixedTimeSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedTimeSeriesQueryUtils.getHierarchicalQuery(geoDataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/geofixedtimeseries/valueQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnFlagsQueryOnChildren_IfQueryDTOHasFlagsQueryDTO() throws IOException, JSONException {

		createFlagsQuery();

		BoolQueryBuilder query = GeoFixedTimeSeriesQueryUtils.getQuery(geoDataQueryDTO,
				GeoFixedTimeSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedTimeSeriesQueryUtils.getHierarchicalQuery(geoDataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/geofixedtimeseries/flagsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	// TODO: recuperar cuando se hagan consultas por z dentro de features
	//@Test
	public void getQuery_ReturnZRangeQuery_IfQueryDTOHasZRangeQueryDTO() throws IOException, JSONException {

		createZRangeQuery();

		BoolQueryBuilder query = GeoFixedTimeSeriesQueryUtils.getQuery(geoDataQueryDTO,
				GeoFixedTimeSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedTimeSeriesQueryUtils.getHierarchicalQuery(geoDataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/geofixedtimeseries/zRangeQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
