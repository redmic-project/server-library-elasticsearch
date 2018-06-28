package es.redmic.test.unit.queryFactory.data;

import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import es.redmic.es.common.queryFactory.data.ActivityQueryUtils;
import es.redmic.test.unit.queryFactory.common.BaseQueryTest;

@RunWith(MockitoJUnitRunner.class)
public class ActivityQueryTest extends BaseQueryTest {

	@Test
	public void getQuery_ReturnInternalQuery_IfIsDefaultQueryDTO() throws IOException {

		BoolQueryBuilder query = ActivityQueryUtils.getQuery(dataQueryDTO, null, null);

		assertNull(query);
	}

	@Test
	public void getQuery_ReturnAccessibilityQuery_IfQueryDTOHasAccessibilityIds() throws IOException, JSONException {

		createAccessibilityQuery();

		BoolQueryBuilder query = ActivityQueryUtils.getQuery(dataQueryDTO, null, null);

		String queryExpected = getExpectedQuery("/queryfactory/data/accessibilityQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnBboxQuery_IfQueryDTOHasBboxDTO() throws IOException, JSONException {

		createBboxQuery();

		BoolQueryBuilder query = ActivityQueryUtils.getQuery(dataQueryDTO, null, null);

		String queryExpected = getExpectedQuery("/queryfactory/data/bBoxQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnDateLimitsQuery_IfQueryDTOHasDateLimisQueryDTO() throws IOException, JSONException {

		createDateLimitsQuery();

		BoolQueryBuilder query = ActivityQueryUtils.getQuery(dataQueryDTO, null, null);

		String queryExpected = getExpectedQuery("/queryfactory/data/dateLimitsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnFlagsQuery_IfQueryDTOHasFlagsQueryDTO() throws IOException, JSONException {

		createFlagsQuery();

		BoolQueryBuilder query = ActivityQueryUtils.getQuery(dataQueryDTO, null, null);

		String queryExpected = getExpectedQuery("/queryfactory/data/flagsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnValueQuery_IfQueryDTOHasValueQueryDTO() throws IOException, JSONException {

		createValueQuery();

		BoolQueryBuilder query = ActivityQueryUtils.getQuery(dataQueryDTO, null, null);

		String queryExpected = getExpectedQuery("/queryfactory/data/valueQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnZRangeQuery_IfQueryDTOHasZRangeQueryDTO() throws IOException, JSONException {

		createZRangeQuery();

		BoolQueryBuilder query = ActivityQueryUtils.getQuery(dataQueryDTO, null, null);

		String queryExpected = getExpectedQuery("/queryfactory/data/zRangeQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnComplexQuery_IfQueryDTOIsFull() throws IOException, JSONException {

		createAccessibilityQuery();
		createBboxQuery();
		createDateLimitsQuery();
		createFlagsQuery();
		createValueQuery();
		createZRangeQuery();

		BoolQueryBuilder query = ActivityQueryUtils.getQuery(dataQueryDTO, null, null);

		String queryExpected = getExpectedQuery("/queryfactory/data/fullQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
