package es.redmic.test.unit.queryFactory.geodata;

import java.io.IOException;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import es.redmic.es.common.queryFactory.geodata.PlatformTrackingQueryUtils;
import es.redmic.test.unit.queryFactory.common.BaseQueryTest;

@RunWith(MockitoJUnitRunner.class)
public class PlatformTrackingQueryTest extends BaseQueryTest {

	protected String parentId = "239";

	@Test
	public void getQuery_ReturnInternalQuery_IfIsDefaultQueryDTO() throws IOException, JSONException {

		BoolQueryBuilder query = PlatformTrackingQueryUtils.getQuery(dataQueryDTO,
				PlatformTrackingQueryUtils.INTERNAL_QUERY,
				PlatformTrackingQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/platformtracking/internalQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
