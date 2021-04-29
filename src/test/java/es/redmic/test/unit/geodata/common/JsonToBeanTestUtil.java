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
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import ma.glasnost.orika.MappingContext;

public class JsonToBeanTestUtil {

	protected ObjectMapper jacksonMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JtsModule());

	public JsonToBeanTestUtil() {
	}

	protected String getJsonString(String filePath) throws IOException {

		return IOUtils.toString(getClass().getResource(filePath).openStream(), Charset.forName("UTF8"));
	}

	protected Object getBean(String path, JavaType dtoClass) throws IOException {

		return jacksonMapper.readValue(getClass().getResource(path).openStream(), dtoClass);
	}

	protected Object getBean(String filePath, Class<?> clazz) throws IOException {

		return jacksonMapper.readValue(getClass().getResource(filePath).openStream(), clazz);
	}

	protected Object getBean(String filePath, TypeReference<?> targetType) throws IOException {

		return jacksonMapper.readValue(getClass().getResource(filePath).openStream(), targetType);
	}

	protected MappingContext getContext(Class<?> dtoClass) {

		Map<Object, Object> globalProperties = new HashMap<Object, Object>();
		globalProperties.put("targetTypeDto", dtoClass);
		return new MappingContext(globalProperties);
	}
}
