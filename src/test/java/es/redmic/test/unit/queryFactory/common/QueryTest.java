package es.redmic.test.unit.queryFactory.common;

import java.io.IOException;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import org.skyscreamer.jsonassert.JSONAssert;

import es.redmic.es.common.queryFactory.geodata.DataQueryUtils;

@RunWith(MockitoJUnitRunner.class)
public class QueryTest extends BaseQueryTest {

	@Test
	public void getQuery_ReturnRegexpQuery_IfQueryDTOHasRegexpDTO() throws IOException, JSONException {

		createRegexpQuery();

		BoolQueryBuilder query = DataQueryUtils.getQuery(dataQueryDTO, null, null);

		String queryExpected = getExpectedQuery("/queryfactory/common/regexpQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnTextQuery_IfQueryDTOHasTextQueryDTO() throws IOException, JSONException {

		createTextQuery();

		BoolQueryBuilder query = DataQueryUtils.getQuery(dataQueryDTO, null, null);

		String queryExpected = getExpectedQuery("/queryfactory/common/textQuery.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}

	@Test
	public void getQuery_ReturnValueQuery_IfQueryDTOHasValueQueryDTOExtended() throws Exception {

		createValueQueryExtended();

		BoolQueryBuilder query = Whitebox.invokeMethod(DataQueryUtils.class, "getValueQuery", dataQueryDTO.getValue(),
				"value");

		String queryExpected = getExpectedQuery("/queryfactory/common/valueQueryExtended.json");

		JSONAssert.assertEquals(queryExpected, query.toString(), false);
	}
}
