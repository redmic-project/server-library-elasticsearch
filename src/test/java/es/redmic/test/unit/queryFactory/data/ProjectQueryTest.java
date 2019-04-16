package es.redmic.test.unit.queryFactory.data;

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

import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import es.redmic.es.common.queryFactory.data.ProjectQueryUtils;
import es.redmic.test.unit.queryFactory.common.BaseQueryTest;

@RunWith(MockitoJUnitRunner.class)
public class ProjectQueryTest extends BaseQueryTest {

	@Test
	public void getQuery_ReturnInternalQuery_IfIsDefaultQueryDTO() throws IOException {

		BoolQueryBuilder query = ProjectQueryUtils.getQuery(dataQueryDTO, null, null);

		assertNull(query);
	}

	@Test
	public void getQuery_ReturnAccessibilityQuery_IfQueryDTOHasAccessibilityIds() throws IOException, JSONException {

		createAccessibilityQuery();

		BoolQueryBuilder query = ProjectQueryUtils.getQuery(dataQueryDTO, null, null);

		String queryExpected = getExpectedQuery("/queryfactory/data/accessibilityQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
