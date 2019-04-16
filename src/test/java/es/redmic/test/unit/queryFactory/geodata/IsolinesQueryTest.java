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

import es.redmic.es.common.queryFactory.geodata.IsolinesQueryUtils;
import es.redmic.test.unit.queryFactory.common.BaseQueryTest;

@RunWith(MockitoJUnitRunner.class)
public class IsolinesQueryTest extends BaseQueryTest {
	
	protected String parentId = "817";
	
	@Test
	public void getQuery_ReturnInternalQuery_IfIsDefaultQueryDTO() throws IOException, JSONException {
		
		BoolQueryBuilder query = IsolinesQueryUtils.getQuery(dataQueryDTO,
				IsolinesQueryUtils.INTERNAL_QUERY, IsolinesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));
		
		String queryExpected = getExpectedQuery("/queryfactory/geodata/isolines/internalQuery.json");
		
		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
	
	@Test
	public void getQuery_ReturnValueQueryOnChildren_IfQueryDTOHasValueQueryDTO() throws IOException, JSONException {
		
		createValueQuery();
		
		BoolQueryBuilder query = IsolinesQueryUtils.getQuery(dataQueryDTO,
				IsolinesQueryUtils.INTERNAL_QUERY, IsolinesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));
		
		String queryExpected = getExpectedQuery("/queryfactory/geodata/isolines/valueQuery.json");
		
		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
	
	@Test
	public void getQuery_ReturnFlagsQueryOnChildren_IfQueryDTOHasFlagsQueryDTO() throws IOException, JSONException {
		
		createFlagsQuery();
		
		BoolQueryBuilder query = IsolinesQueryUtils.getQuery(dataQueryDTO,
				IsolinesQueryUtils.INTERNAL_QUERY, IsolinesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));
		
		String queryExpected = getExpectedQuery("/queryfactory/geodata/isolines/flagsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
