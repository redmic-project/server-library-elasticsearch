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
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import es.redmic.es.common.queryFactory.geodata.GeoFixedObjectCollectingSeriesQueryUtils;
import es.redmic.test.unit.queryFactory.common.BaseQueryTest;

@RunWith(MockitoJUnitRunner.class)
public class GeoFixedObjectCollectingSeriesQueryTest extends BaseQueryTest {

	protected String parentId = "239";

	@Test
	public void getQuery_ReturnInternalQuery_IfIsDefaultQueryDTO() throws IOException, JSONException {

		BoolQueryBuilder query = GeoFixedObjectCollectingSeriesQueryUtils.getQuery(dataQueryDTO,
				GeoFixedObjectCollectingSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedObjectCollectingSeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery(
				"/queryfactory/geodata/geofixedobjectcollectingseries/internalQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnDateLimitsQueryOnChildren_IfQueryDTOHasDateLimisQueryDTO()
			throws IOException, JSONException {

		createDateLimitsQuery();

		BoolQueryBuilder query = GeoFixedObjectCollectingSeriesQueryUtils.getQuery(dataQueryDTO,
				GeoFixedObjectCollectingSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedObjectCollectingSeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery(
				"/queryfactory/geodata/geofixedobjectcollectingseries/dateLimitsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnValueQueryOnChildren_IfQueryDTOHasValueQueryDTO() throws IOException, JSONException {

		createValueQuery();

		BoolQueryBuilder query = GeoFixedObjectCollectingSeriesQueryUtils.getQuery(dataQueryDTO,
				GeoFixedObjectCollectingSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedObjectCollectingSeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/geofixedobjectcollectingseries/valueQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnFlagsQueryOnChildren_IfQueryDTOHasFlagsQueryDTO() throws IOException, JSONException {

		createFlagsQuery();

		BoolQueryBuilder query = GeoFixedObjectCollectingSeriesQueryUtils.getQuery(dataQueryDTO,
				GeoFixedObjectCollectingSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedObjectCollectingSeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/geofixedobjectcollectingseries/flagsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnZRangeQuery_IfQueryDTOHasZRangeQueryDTO() throws IOException, JSONException {

		createZRangeQuery();

		BoolQueryBuilder query = GeoFixedObjectCollectingSeriesQueryUtils.getQuery(dataQueryDTO,
				GeoFixedObjectCollectingSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedObjectCollectingSeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery(
				"/queryfactory/geodata/geofixedobjectcollectingseries/zRangeQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
