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
import org.mockito.junit.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.type.TypeReference;
import org.locationtech.jts.geom.Geometry;

import es.redmic.es.geodata.common.converter.CategoryListConverter;
import es.redmic.models.es.geojson.common.dto.CategoryListDTO;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.test.utils.OrikaScanBeanTest;

@RunWith(MockitoJUnitRunner.class)
public class CategoryListConverterTest extends JsonToBeanTestUtil {

	protected OrikaScanBeanTest factory = new OrikaScanBeanTest();

	String modelInPath = "/geodata/common/model/categoriesAggregationsIn.json",
			dtoOutPath = "/geodata/common/dto/categoriesDTO.json";

	@Before
	public void setupTest() {

		factory.addConverter(new CategoryListConverter());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void convertAggregations2Dto() throws IOException, JSONException {

		TypeReference<GeoSearchWrapper<GeoDataProperties, Geometry>> type = new TypeReference<GeoSearchWrapper<GeoDataProperties, Geometry>>() {
		};

		GeoSearchWrapper result = (GeoSearchWrapper) getBean(modelInPath, type);

		Object dtoOut = factory.getMapperFacade().convert(result.getAggregations(), CategoryListDTO.class, null, null);
		String dtoStringExpected = getJsonString(dtoOutPath);

		JSONAssert.assertEquals(jacksonMapper.writeValueAsString(dtoOut), dtoStringExpected, false);
	}
}
