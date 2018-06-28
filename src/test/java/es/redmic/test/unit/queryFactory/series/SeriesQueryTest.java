package es.redmic.test.unit.queryFactory.series;

import java.io.IOException;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import es.redmic.es.common.queryFactory.series.SeriesQueryUtils;
import es.redmic.test.unit.queryFactory.common.BaseQueryTest;

@RunWith(MockitoJUnitRunner.class)
public class SeriesQueryTest extends BaseQueryTest {

	protected String parentId = "AS65565sWEWEsd", grandparentId = "239";

	@Test
	public void getQuery_ReturnDefaultQuery_IfIsDefaultQueryDTO() throws IOException, JSONException {

		BoolQueryBuilder query = SeriesQueryUtils.getQuery(dataQueryDTO, SeriesQueryUtils.INTERNAL_QUERY,
				SeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId, grandparentId));

		String queryExpected = getExpectedQuery("/queryfactory/series/internalQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnDateLimitsQuery_IfQueryDTOHasDateLimisQueryDTO() throws IOException, JSONException {

		createDateLimitsQuery();

		BoolQueryBuilder query = SeriesQueryUtils.getQuery(dataQueryDTO, SeriesQueryUtils.INTERNAL_QUERY,
				SeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/series/dateLimitsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnFlagsQuery_IfQueryDTOHasFlagsQueryDTO() throws IOException, JSONException {

		createFlagsQuery();

		BoolQueryBuilder query = SeriesQueryUtils.getQuery(dataQueryDTO, SeriesQueryUtils.INTERNAL_QUERY,
				SeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/series/flagsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnValueQuery_IfQueryDTOHasValueQueryDTO() throws IOException, JSONException {

		createValueQuery();

		BoolQueryBuilder query = SeriesQueryUtils.getQuery(dataQueryDTO, SeriesQueryUtils.INTERNAL_QUERY,
				SeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/series/valueQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnZRangeQuery_IfQueryDTOHasZRangeQueryDTO() throws IOException, JSONException {

		createZRangeQuery();

		BoolQueryBuilder query = SeriesQueryUtils.getQuery(dataQueryDTO, SeriesQueryUtils.INTERNAL_QUERY,
				SeriesQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/series/zRangeQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnComplexQuery_IfQueryDTOIsFull() throws IOException, JSONException {

		createDateLimitsQuery();
		createFlagsQuery();
		createValueQuery();
		createZRangeQuery();

		BoolQueryBuilder query = SeriesQueryUtils.getQuery(dataQueryDTO, null, null);

		String queryExpected = getExpectedQuery("/queryfactory/series/fullQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
