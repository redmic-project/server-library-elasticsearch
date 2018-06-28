package es.redmic.test.unit.queryFactory.data;

import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import es.redmic.es.common.queryFactory.data.ProgramQueryUtils;
import es.redmic.test.unit.queryFactory.common.BaseQueryTest;

@RunWith(MockitoJUnitRunner.class)
public class ProgramQueryTest extends BaseQueryTest {

	@Test
	public void getQuery_ReturnInternalQuery_IfIsDefaultQueryDTO() throws IOException {

		BoolQueryBuilder query = ProgramQueryUtils.getQuery(dataQueryDTO, null, null);

		assertNull(query);
	}

	@Test
	public void getQuery_ReturnAccessibilityQuery_IfQueryDTOHasAccessibilityIds() throws IOException, JSONException {

		createAccessibilityQuery();

		BoolQueryBuilder query = ProgramQueryUtils.getQuery(dataQueryDTO, null, null);

		String queryExpected = getExpectedQuery("/queryfactory/data/accessibilityQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
