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
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.test.utils.OrikaScanBeanTest;

public class MapperTestUtil extends JsonToBeanTestUtil {

	protected OrikaScanBeanTest factory = new OrikaScanBeanTest();

	public MapperTestUtil() {
	}

	public void mapperDtoToModel(String dtoInPath, String modelOutPath, Class<?> dtoClass, Class<?> modelClass)
			throws JsonParseException, JsonMappingException, IOException, JSONException {

		Object dtoIn = getBean(dtoInPath, dtoClass);

		Object modelOut = factory.getMapperFacade().map(dtoIn, modelClass);

		String modelStringExpected = getJsonString(modelOutPath);
		String modelString = jacksonMapper.writeValueAsString(modelOut);
		
		JSONAssert.assertEquals(modelString, modelStringExpected, false);
	}
}
