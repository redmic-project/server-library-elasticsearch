package es.redmic.test.unit.queryFactory.geodata;

import java.io.IOException;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import es.redmic.es.common.queryFactory.geodata.GeoFixedTimeSeriesQueryUtils;
import es.redmic.test.unit.queryFactory.common.BaseQueryTest;

@RunWith(MockitoJUnitRunner.class)
public class GeoFixedTimeSeriesQueryTest extends BaseQueryTest {

	protected String parentId = "239";

	@Test
	public void getQuery_ReturnInternalQuery_IfIsDefaultQueryDTO() throws IOException, JSONException {

		BoolQueryBuilder query = GeoFixedTimeSeriesQueryUtils.getQuery(dataQueryDTO,
				GeoFixedTimeSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedTimeSeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/geofixedtimeseries/internalQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnDateLimitsQueryOnChildren_IfQueryDTOHasDateLimisQueryDTO()
			throws IOException, JSONException {

		createDateLimitsQuery();

		BoolQueryBuilder query = GeoFixedTimeSeriesQueryUtils.getQuery(dataQueryDTO,
				GeoFixedTimeSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedTimeSeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/geofixedtimeseries/dateLimitsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnValueQueryOnChildren_IfQueryDTOHasValueQueryDTO() throws IOException, JSONException {

		createValueQuery();

		BoolQueryBuilder query = GeoFixedTimeSeriesQueryUtils.getQuery(dataQueryDTO,
				GeoFixedTimeSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedTimeSeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/geofixedtimeseries/valueQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnFlagsQueryOnChildren_IfQueryDTOHasFlagsQueryDTO() throws IOException, JSONException {

		createFlagsQuery();

		BoolQueryBuilder query = GeoFixedTimeSeriesQueryUtils.getQuery(dataQueryDTO,
				GeoFixedTimeSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedTimeSeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/geofixedtimeseries/flagsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnZRangeQuery_IfQueryDTOHasZRangeQueryDTO() throws IOException, JSONException {

		createZRangeQuery();

		BoolQueryBuilder query = GeoFixedTimeSeriesQueryUtils.getQuery(dataQueryDTO,
				GeoFixedTimeSeriesQueryUtils.INTERNAL_QUERY,
				GeoFixedTimeSeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/geofixedtimeseries/zRangeQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
