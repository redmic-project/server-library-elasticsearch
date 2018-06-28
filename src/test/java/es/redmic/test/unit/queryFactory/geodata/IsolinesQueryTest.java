package es.redmic.test.unit.queryFactory.geodata;

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
