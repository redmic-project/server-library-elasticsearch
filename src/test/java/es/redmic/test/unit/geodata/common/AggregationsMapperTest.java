package es.redmic.test.unit.geodata.common;

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

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.es.common.mapper.AggregationsMapper;
import es.redmic.models.es.common.dto.AggregationsDTO;
import es.redmic.models.es.geojson.common.model.Aggregations;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@RunWith(MockitoJUnitRunner.class)
public class AggregationsMapperTest extends JsonToBeanTestUtil {

	String modelInPath = "/geodata/common/model/aggregationsIn.json",
			dtoOutPath = "/geodata/common/dto/aggregationsOut.json";

	protected MapperFactory factory = new DefaultMapperFactory.Builder().build();

	protected ConverterFactory converterFactory = factory.getConverterFactory();

	private AggregationsMapper mapper = new AggregationsMapper();

	@Before
	public void setupTest() throws IOException {
		MockitoAnnotations.initMocks(this);

		factory.classMap(Aggregations.class, AggregationsDTO.class)
			.byDefault()
				.customize(mapper)
					.register();
	}

	@Test
	public void mapperModelToDTO() throws JsonParseException, JsonMappingException, IOException, JSONException {

		Aggregations modelIn = (Aggregations) getBean(modelInPath, Aggregations.class);

		AggregationsDTO modelOut = factory.getMapperFacade().map(modelIn, AggregationsDTO.class);

		String dtoStringExpected = getJsonString(dtoOutPath);

		JSONAssert.assertEquals(jacksonMapper.writeValueAsString(modelOut), dtoStringExpected, false);
	}
}
