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

import static org.mockito.ArgumentMatchers.any;
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

import es.redmic.es.administrative.service.ContactESService;
import es.redmic.es.geodata.geofixedstation.mapper.DataDefinitionESMapper;
import es.redmic.es.maintenance.device.service.DeviceESService;
import es.redmic.es.maintenance.domain.administrative.service.ContactRoleESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.ConfidenceESService;
import es.redmic.models.es.administrative.model.Contact;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.device.model.Device;
import es.redmic.models.es.maintenance.parameter.dto.DataDefinitionDTO;
import es.redmic.models.es.maintenance.parameter.model.DataDefinition;

@RunWith(MockitoJUnitRunner.class)
public class DataDefinitionMapperTest extends MapperTestUtil {

	@Mock
	DeviceESService deviceService;

	@Mock
	ConfidenceESService confidenceService;

	@Mock
	ContactESService contactService;

	@Mock
	ContactRoleESService contactRoleService;

	@InjectMocks
	DataDefinitionESMapper dataDefinitionMapper;

	String modelOutPath = "/geodata/common/model/dataDefinition.json",
			dtoInPath = "/geodata/common/dto/dataDefinitionIn.json",
			deviceCompactModel = "/data/administrative/device/model/deviceCompact.json",
			confidenceModel = "/geodata/common/model/confidence.json",
			contactCompactModel = "/data/administrative/contact/model/contactCompact.json",
			contactRoleModel = "/data/administrative/contact/model/contactRole.json";

	@Before
	public void setupTest() throws IOException {

		factory.addMapper(dataDefinitionMapper);

		Device deviceExpected = (Device) getBean(deviceCompactModel, Device.class);
		// DomainES confidence = (DomainES) getBean(confidenceModel, DomainES.class);
		Contact contactExpected = (Contact) getBean(contactCompactModel, Contact.class);
		DomainES contactRoleExpected = (DomainES) getBean(contactRoleModel, DomainES.class);

		when(deviceService.findById(any(String.class))).thenReturn(deviceExpected);
		// when(confidenceService.findById(any(String.class))).thenReturn(confidence);
		when(contactService.findById(any(String.class))).thenReturn(contactExpected);
		when(contactRoleService.findById(any(String.class))).thenReturn(contactRoleExpected);

	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, DataDefinitionDTO.class, DataDefinition.class);
	}
}
