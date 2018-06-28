package es.redmic.test.unit.geodata.geofixedstation;

import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.junit.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.es.administrative.service.ContactESService;
import es.redmic.es.geodata.geofixedstation.mapper.DataDefinitionESMapper;
import es.redmic.es.geodata.geofixedstation.mapper.FixedSurveySeriesPropertiesESMapper;
import es.redmic.es.geodata.geofixedstation.mapper.MeasurementESMapper;
import es.redmic.es.geodata.geofixedstation.mapper.SiteESMapper;
import es.redmic.es.maintenance.device.service.DeviceESService;
import es.redmic.es.maintenance.domain.administrative.service.ContactRoleESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.ConfidenceESService;
import es.redmic.es.maintenance.objects.service.ObjectTypeESService;
import es.redmic.es.maintenance.parameter.service.ParameterESService;
import es.redmic.es.maintenance.parameter.service.UnitESService;
import es.redmic.models.es.administrative.model.Contact;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.geojson.common.model.GeoLineStringData;
import es.redmic.models.es.geojson.geofixedstation.dto.GeoFixedObjectCollectingSeriesDTO;
import es.redmic.models.es.maintenance.device.model.Device;
import es.redmic.models.es.maintenance.parameter.model.Parameter;
import es.redmic.models.es.maintenance.parameter.model.Unit;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.MappingContext;

@RunWith(MockitoJUnitRunner.class)
public class GeoFixedObjectCollectingSeriesMapperTest extends MapperTestUtil {

	@Mock
	DeviceESService deviceService;

	@Mock
	ParameterESService parameterESService;

	@Mock
	UnitESService unitESService;

	@Mock
	ConfidenceESService confidenceService;

	@Mock
	ContactESService contactService;

	@Mock
	ContactRoleESService contactRoleService;

	@Mock
	ObjectTypeESService objectService;

	@InjectMocks
	FixedSurveySeriesPropertiesESMapper mapper;

	@InjectMocks
	SiteESMapper mapperSite;

	@InjectMocks
	DataDefinitionESMapper dataDefinitionMapper;

	@InjectMocks
	MeasurementESMapper measurementMapper;

	String modelOutPath = "/geodata/objectcollecting/model/geoFixedObjectCollectingSeries.json",
			dtoInPath = "/geodata/objectcollecting/dto/geoFixedObjectCollectingSeriesIn.json",
			deviceCompactModel = "/data/administrative/device/model/deviceCompact.json",
			parameterBaseModel = "/data/administrative/parameter/model/parameterBase5.json",
			unitModel = "/data/administrative/parameter/model/unit5.json",
			contactCompactModel = "/data/administrative/contact/model/contactCompact.json",
			contactRoleModel = "/data/administrative/contact/model/contactRole.json";

	@Before
	public void setupTest() throws IOException {

		factory.addMapper(new FixedSurveySeriesPropertiesESMapper());
		factory.addMapper(measurementMapper);
		factory.addMapper(dataDefinitionMapper);
		factory.addMapper(mapperSite);

		Device deviceExpected = (Device) getBean(deviceCompactModel, Device.class);
		Contact contactExpected = (Contact) getBean(contactCompactModel, Contact.class);
		DomainES contactRoleExpected = (DomainES) getBean(contactRoleModel, DomainES.class);

		Parameter parameterExpected = (Parameter) getBean(parameterBaseModel, Parameter.class);
		Unit unitExpected = (Unit) getBean(unitModel, Unit.class);

		when(deviceService.findById(any(String.class))).thenReturn(deviceExpected);
		when(contactService.findById(any(String.class))).thenReturn(contactExpected);
		when(contactRoleService.findById(any(String.class))).thenReturn(contactRoleExpected);

		when(parameterESService.findById(any(String.class))).thenReturn(parameterExpected);
		when(unitESService.findById(any(String.class))).thenReturn(unitExpected);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		Map<Object, Object> globalProperties = new HashMap<Object, Object>();

		GeoFixedObjectCollectingSeriesDTO dtoIn = (GeoFixedObjectCollectingSeriesDTO) getBean(dtoInPath,
				GeoFixedObjectCollectingSeriesDTO.class);

		globalProperties.put("uuid", dtoIn.getUuid());
		globalProperties.put("geoDataPrefix", DataPrefixType.OBJECT_COLLECTING);
		MappingContext context = new MappingContext(globalProperties);

		GeoLineStringData modelOut = factory.getMapperFacade().map(dtoIn, GeoLineStringData.class, context);

		String modelStringExpected = getJsonString(modelOutPath);

		JSONAssert.assertEquals(jacksonMapper.writeValueAsString(modelOut), modelStringExpected, false);
	}
}
