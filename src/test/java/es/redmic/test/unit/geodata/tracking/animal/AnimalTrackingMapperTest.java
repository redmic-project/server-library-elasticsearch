package es.redmic.test.unit.geodata.tracking.animal;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.es.administrative.taxonomy.service.AnimalESService;
import es.redmic.es.geodata.tracking.animal.mapper.AnimalTrackingESMapper;
import es.redmic.es.geodata.tracking.animal.mapper.AnimalTrackingPropertiesESMapper;
import es.redmic.es.maintenance.device.service.DeviceESService;
import es.redmic.models.es.administrative.taxonomy.model.Animal;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.tracking.animal.dto.AnimalTrackingDTO;
import es.redmic.models.es.maintenance.device.model.Device;
import es.redmic.test.unit.geodata.common.MapperTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class AnimalTrackingMapperTest extends MapperTestUtil {

	@Mock
	AnimalESService animalService;

	@Mock
	DeviceESService deviceService;

	@InjectMocks
	AnimalTrackingESMapper featureMapper;

	@InjectMocks
	AnimalTrackingPropertiesESMapper mapper;

	String modelOutPath = "/geodata/animalTracking/model/animalTracking.json",
			dtoInPath = "/geodata/animalTracking/dto/animalTrackingIn.json",
			deviceCompactModel = "/data/administrative/device/model/deviceCompact.json",
			animalCompactModel = "/data/taxonomy/model/animal.json";

	@Before
	public void setupTest() throws IOException {

		factory.addMapper(mapper);
		factory.addMapper(featureMapper);

		Animal animalExpected = (Animal) getBean(animalCompactModel, Animal.class);
		Device deviceExpected = (Device) getBean(deviceCompactModel, Device.class);

		when(animalService.findById(any(String.class))).thenReturn(animalExpected);
		when(deviceService.findById(any(String.class))).thenReturn(deviceExpected);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, AnimalTrackingDTO.class, GeoPointData.class);
	}
}
