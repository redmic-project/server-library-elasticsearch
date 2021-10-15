package es.redmic.test.unit.tools.distribution;

/*-
 * #%L
 * ElasticSearch
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.mock.mockito.MockBean;

import es.redmic.es.common.queryFactory.geodata.AnimalTrackingQueryUtils;
import es.redmic.es.tools.distributions.species.repository.TaxonDist100MRepository;
import es.redmic.models.es.common.query.dto.BboxQueryDTO;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;
import es.redmic.test.unit.queryFactory.common.GeoDataQueryTest;

@RunWith(MockitoJUnitRunner.class)
public class SpeciesDistributionQueryTest extends GeoDataQueryTest {

	TaxonDist100MRepository repository = new TaxonDist100MRepository();

	@Test
	public void getQuery_ReturnInternalQuery_IfIsDefaultQueryDTO() throws IOException, JSONException {

		GeoDataQueryDTO dto = new GeoDataQueryDTO();

		BboxQueryDTO bbox = new BboxQueryDTO();
		bbox.setBottomRightLat(27.806396484375);
		bbox.setBottomRightLon(-12.80181884765625);
		bbox.setTopLeftLat(30.0421142578125);
		bbox.setTopLeftLon(-18.63104248046875);

		dto.setBbox(bbox);

		List<String> ids = new ArrayList<>();
		ids.add("root.1.13.148.19923.455.1570.4833.14618");
		List<Integer> confidence = new ArrayList<>();
		confidence.add(4);

		Map<String, Object> scriptParams = new HashMap<>();
		scriptParams.put("taxons", ids);
		scriptParams.put("confidences", confidence);


		SearchRequest query = repository.createQuery(dto, ids, confidence, new String[] { "*" }, new String[] {}, scriptParams);

		String queryExpected = getExpectedQuery("/queryfactory/tools/distribution/distributionQuery.json");

		JSONAssert.assertEquals(queryExpected, query.source().toString(), false);
	}
}
