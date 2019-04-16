package es.redmic.test.unit.queryFactory.common;

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
import org.powermock.reflect.Whitebox;
import org.skyscreamer.jsonassert.JSONAssert;

import es.redmic.es.common.queryFactory.geodata.DataQueryUtils;

@RunWith(MockitoJUnitRunner.class)
public class QueryTest extends BaseQueryTest {

	@Test
	public void getQuery_ReturnRegexpQuery_IfQueryDTOHasRegexpDTO() throws IOException, JSONException {

		createRegexpQuery();

		BoolQueryBuilder query = DataQueryUtils.getQuery(dataQueryDTO, null, null);

		String queryExpected = getExpectedQuery("/queryfactory/common/regexpQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnTextQuery_IfQueryDTOHasTextQueryDTO() throws IOException, JSONException {

		createTextQuery();

		BoolQueryBuilder query = DataQueryUtils.getQuery(dataQueryDTO, null, null);

		String queryExpected = getExpectedQuery("/queryfactory/common/textQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnValueQuery_IfQueryDTOHasValueQueryDTOExtended() throws Exception {

		createValueQueryExtended();

		BoolQueryBuilder query = Whitebox.invokeMethod(DataQueryUtils.class, "getValueQuery", dataQueryDTO.getValue(),
				"value");

		String queryExpected = getExpectedQuery("/queryfactory/common/valueQueryExtended.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
