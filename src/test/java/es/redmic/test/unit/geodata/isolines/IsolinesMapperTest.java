package es.redmic.test.unit.geodata.isolines;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.es.geodata.geofixedstation.mapper.DataDefinitionESMapper;
import es.redmic.es.geodata.geofixedstation.mapper.MeasurementESMapper;
import es.redmic.es.geodata.isolines.converter.LineTypeClassificationESConverter;
import es.redmic.es.geodata.isolines.mapper.IsolinesESMapper;
import es.redmic.es.geodata.isolines.mapper.IsolinesPropertiesESMapper;
import es.redmic.es.maintenance.device.service.DeviceESService;
import es.redmic.es.maintenance.line.service.LineTypeESService;
import es.redmic.es.maintenance.parameter.service.ParameterESService;
import es.redmic.es.maintenance.parameter.service.UnitESService;
import es.redmic.es.series.common.converter.DataDefinitionConverter;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.geojson.common.model.GeoMultiLineStringData;
import es.redmic.models.es.geojson.isolines.dto.IsolinesDTO;
import es.redmic.models.es.maintenance.device.model.Device;
import es.redmic.models.es.maintenance.line.model.LineType;
import es.redmic.models.es.maintenance.parameter.model.Parameter;
import es.redmic.models.es.maintenance.parameter.model.Unit;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.MappingContext;

@RunWith(MockitoJUnitRunner.class)
public class IsolinesMapperTest extends MapperTestUtil {

	@Mock
	ParameterESService parameterESService;

	@Mock
	UnitESService unitESService;

	@Mock
	DeviceESService deviceService;

	@Mock
	LineTypeESService lineTypeESService;

	@InjectMocks
	LineTypeClassificationESConverter converter;

	@InjectMocks
	DataDefinitionConverter dataDefinitionConverter;

	@InjectMocks
	DataDefinitionESMapper dataDefinitionMapper;

	@InjectMocks
	MeasurementESMapper measurementMapper;

	@InjectMocks
	IsolinesESMapper featureMapper;

	@InjectMocks
	IsolinesPropertiesESMapper mapper;

	String modelOutPath = "/geodata/isolines/model/isolines.json", dtoInPath = "/geodata/isolines/dto/isolinesIn.json",
			parameterBaseModel = "/geodata/isolines/model/parameterBase.json",
			unitModel = "/geodata/isolines/model/unit.json",
			deviceCompactModel = "/data/administrative/device/model/deviceCompact.json",
			classificationModel9 = "/geodata/isolines/model/classification9.json",
			classificationModel10 = "/geodata/isolines/model/classification10.json";

	@Before
	public void setupTest() throws IOException {

		factory.addMapper(dataDefinitionMapper);
		factory.addMapper(measurementMapper);
		factory.addMapper(mapper);
		factory.addMapper(featureMapper);
		factory.addConverter(dataDefinitionConverter);
		factory.addConverter(converter);

		Parameter parameterExpected = (Parameter) getBean(parameterBaseModel, Parameter.class);
		Unit unitExpected = (Unit) getBean(unitModel, Unit.class);
		Device deviceExpected = (Device) getBean(deviceCompactModel, Device.class);
		LineType classification9 = (LineType) getBean(classificationModel9, LineType.class),
				classification10 = (LineType) getBean(classificationModel10, LineType.class);

		when(parameterESService.findById(any(String.class))).thenReturn(parameterExpected);
		when(unitESService.findById(any(String.class))).thenReturn(unitExpected);
		when(deviceService.findById(any(String.class))).thenReturn(deviceExpected);
		when(lineTypeESService.findById("9")).thenReturn(classification9);
		when(lineTypeESService.findById("10")).thenReturn(classification10);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		Map<Object, Object> globalProperties = new HashMap<Object, Object>();

		IsolinesDTO dtoIn = (IsolinesDTO) getBean(dtoInPath, IsolinesDTO.class);

		globalProperties.put("uuid", dtoIn.getUuid());
		globalProperties.put("geoDataPrefix", DataPrefixType.ISOLINES);
		MappingContext context = new MappingContext(globalProperties);

		GeoMultiLineStringData modelOut = factory.getMapperFacade().map(dtoIn, GeoMultiLineStringData.class, context);

		String modelStringExpected = getJsonString(modelOutPath);

		JSONAssert.assertEquals(jacksonMapper.writeValueAsString(modelOut), modelStringExpected, false);
	}
}
