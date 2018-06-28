package es.redmic.test.unit.series.attributeseries;

import static org.mockito.Mockito.when;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.es.maintenance.qualifiers.service.AttributeTypeESService;
import es.redmic.es.series.attributeseries.mapper.AttributeSeriesESMapper;
import es.redmic.models.es.maintenance.qualifiers.model.AttributeType;
import es.redmic.models.es.series.attributeseries.dto.AttributeSeriesDTO;
import es.redmic.models.es.series.attributeseries.model.AttributeSeries;
import es.redmic.test.unit.geodata.common.MapperTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class AttributeSeriesMapperTest extends MapperTestUtil {

	String modelOutPath = "/series/attributeseries/model/attributeSeries.json",
			dtoInPath = "/series/attributeseries/dto/attributeSeriesIn.json",
			attributeTypePath10 = "/data/maintenance/attributetype/model/attributetypebase10.json",
			attributeTypePath11 = "/data/maintenance/attributetype/model/attributetypebase11.json";

	@Mock
	AttributeTypeESService attributeTypeESService;

	@InjectMocks
	AttributeSeriesESMapper mapper;

	@Before
	public void setupTest() throws IOException {

		factory.addMapper(mapper);

		AttributeType attributeType10 = (AttributeType) getBean(attributeTypePath10,
				AttributeType.class)/*
									 * , attributeType11 = (AttributeType) getBean(attributeTypePath11,
									 * AttributeType.class)
									 */;

		when(attributeTypeESService.findById("10")).thenReturn(attributeType10);
		// when(attributeTypeESService.findById("11")).thenReturn(attributeType11);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, AttributeSeriesDTO.class, AttributeSeries.class);
	}
}
