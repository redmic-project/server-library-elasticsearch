package es.redmic.test.unit.queryFactory.geodata;

import java.io.IOException;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import es.redmic.es.common.queryFactory.geodata.DataQueryUtils;
import es.redmic.es.common.queryFactory.geodata.InfrastructureQueryUtils;
import es.redmic.test.unit.queryFactory.common.BaseQueryTest;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureQueryTest extends BaseQueryTest {

	protected String parentId = "239";

	@Test
	public void getQuery_ReturnInternalQuery_IfIsDefaultQueryDTO() throws IOException, JSONException {

		BoolQueryBuilder query = InfrastructureQueryUtils.getQuery(dataQueryDTO,
				InfrastructureQueryUtils.INTERNAL_QUERY, DataQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/infrastructure/internalQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnZRangeQuery_IfQueryDTOHasZRangeQueryDTO() throws IOException, JSONException {

		createZRangeQuery();

		BoolQueryBuilder query = InfrastructureQueryUtils.getQuery(dataQueryDTO,
				InfrastructureQueryUtils.INTERNAL_QUERY, DataQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/infrastructure/zRangeQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnDateLimitsQuery_IfQueryDTOHasDateLimisQueryDTO() throws IOException, JSONException {

		createDateLimitsQuery();

		BoolQueryBuilder query = InfrastructureQueryUtils.getQuery(dataQueryDTO,
				InfrastructureQueryUtils.INTERNAL_QUERY, DataQueryUtils.getHierarchicalQuery(dataQueryDTO, parentId));

		String queryExpected = getExpectedQuery("/queryfactory/geodata/infrastructure/dateLimitsQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
