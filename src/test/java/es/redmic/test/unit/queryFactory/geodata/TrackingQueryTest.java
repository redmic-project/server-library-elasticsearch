package es.redmic.test.unit.queryFactory.geodata;

import java.io.IOException;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import es.redmic.es.common.queryFactory.geodata.TrackingQueryUtils;
import es.redmic.test.unit.queryFactory.common.BaseQueryTest;

@RunWith(MockitoJUnitRunner.class)
public class TrackingQueryTest extends BaseQueryTest {

	protected String parentId = "239";

	@Test
	public void getQuery_ReturnInternalQuery_IfIsDefaultQueryDTO() throws IOException, JSONException {

		BoolQueryBuilder query = TrackingQueryUtils.getQuery(dataQueryDTO, TrackingQueryUtils.INTERNAL_QUERY,
				TrackingQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/tracking/internalQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnZRangeQuery_IfQueryDTOHasZRangeQueryDTO() throws IOException, JSONException {

		createZRangeQuery();

		BoolQueryBuilder query = TrackingQueryUtils.getQuery(dataQueryDTO, TrackingQueryUtils.INTERNAL_QUERY,
				TrackingQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/tracking/zRangeQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnDateLimitsQuery_IfQueryDTOHasDateLimisQueryDTO() throws IOException, JSONException {

		createDateLimitsQuery();

		BoolQueryBuilder query = TrackingQueryUtils.getQuery(dataQueryDTO, TrackingQueryUtils.INTERNAL_QUERY,
				TrackingQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/tracking/dateLimitsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnFlagsQueryOnChildren_IfQueryDTOHasFlagsQueryDTO() throws IOException, JSONException {

		createFlagsQuery();

		BoolQueryBuilder query = TrackingQueryUtils.getQuery(dataQueryDTO, TrackingQueryUtils.INTERNAL_QUERY,
				TrackingQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/tracking/flagsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnPrecisionQuery_IfQueryDTOHasPrecisionQueryDTO() throws IOException, JSONException {

		createPrecisionQuery();

		BoolQueryBuilder query = TrackingQueryUtils.getQuery(dataQueryDTO, TrackingQueryUtils.INTERNAL_QUERY,
				TrackingQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/tracking/precisionQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
