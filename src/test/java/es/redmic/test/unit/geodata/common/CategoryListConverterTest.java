package es.redmic.test.unit.geodata.common;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vividsolutions.jts.geom.Geometry;

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

		Object dtoOut = factory.getMapperFacade().convert(result.getAggregations(), CategoryListDTO.class, null);
		String dtoStringExpected = getJsonString(dtoOutPath);

		JSONAssert.assertEquals(jacksonMapper.writeValueAsString(dtoOut), dtoStringExpected, false);
	}
}