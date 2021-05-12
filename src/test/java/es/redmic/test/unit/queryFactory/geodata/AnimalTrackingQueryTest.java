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

import es.redmic.es.common.queryFactory.geodata.AnimalTrackingQueryUtils;
import es.redmic.test.unit.queryFactory.common.GeoDataQueryTest;

@RunWith(MockitoJUnitRunner.class)
public class AnimalTrackingQueryTest extends GeoDataQueryTest {

	protected String parentId = "239";

	@Test
	public void getQuery_ReturnInternalQuery_IfIsDefaultQueryDTO() throws IOException, JSONException {

		BoolQueryBuilder query = AnimalTrackingQueryUtils.getQuery(geoDataQueryDTO,
				AnimalTrackingQueryUtils.INTERNAL_QUERY,
				AnimalTrackingQueryUtils.getHierarchicalQuery(geoDataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/animaltracking/internalQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
