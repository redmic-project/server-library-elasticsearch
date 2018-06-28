package es.redmic.test.unit.queryFactory.geodata;

import java.io.IOException;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import es.redmic.es.common.queryFactory.geodata.CitationQueryUtils;
import es.redmic.es.common.queryFactory.geodata.DataQueryUtils;
import es.redmic.test.unit.queryFactory.common.BaseQueryTest;

@RunWith(MockitoJUnitRunner.class)
public class CitationQueryTest extends BaseQueryTest {

	protected String parentId = "239";

	@Test
	public void getQuery_ReturnInternalQuery_IfIsDefaultQueryDTO() throws IOException, JSONException {

		BoolQueryBuilder query = CitationQueryUtils.getQuery(dataQueryDTO, CitationQueryUtils.INTERNAL_QUERY,
				DataQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/citation/internalQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnZRangeQuery_IfQueryDTOHasZRangeQueryDTO() throws IOException, JSONException {

		createZRangeQuery();

		BoolQueryBuilder query = CitationQueryUtils.getQuery(dataQueryDTO, CitationQueryUtils.INTERNAL_QUERY,
				DataQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/citation/zRangeQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnDateLimitsQuery_IfQueryDTOHasDateLimisQueryDTO() throws IOException, JSONException {

		createDateLimitsQuery();

		BoolQueryBuilder query = CitationQueryUtils.getQuery(dataQueryDTO, CitationQueryUtils.INTERNAL_QUERY,
				DataQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/citation/dateLimitsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnPrecisionQuery_IfQueryDTOHasPrecisionQueryDTO() throws IOException, JSONException {

		createPrecisionQuery();

		BoolQueryBuilder query = CitationQueryUtils.getQuery(dataQueryDTO, CitationQueryUtils.INTERNAL_QUERY,
				DataQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/citation/precisionQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
