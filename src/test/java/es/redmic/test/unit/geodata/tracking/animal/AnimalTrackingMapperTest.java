package es.redmic.test.unit.geodata.tracking.animal;

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

import static org.mockito.Matchers.any;
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
