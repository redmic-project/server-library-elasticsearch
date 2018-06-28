package es.redmic.test.unit.geodata.common;

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