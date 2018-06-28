package es.redmic.test.unit.geodata.common;

import java.io.IOException;
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

		return IOUtils.toString(getClass().getResource(filePath).openStream());
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