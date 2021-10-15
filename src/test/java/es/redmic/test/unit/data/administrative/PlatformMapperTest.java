package es.redmic.test.unit.data.administrative;

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

import static org.mockito.ArgumentMatchers.anyString;
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

import es.redmic.es.administrative.mapper.PlatformESMapper;
import es.redmic.es.administrative.service.ContactESService;
import es.redmic.es.administrative.service.OrganisationESService;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.maintenance.domain.administrative.service.ContactRoleESService;
import es.redmic.es.maintenance.domain.administrative.service.PlatformTypeESService;
import es.redmic.models.es.administrative.dto.PlatformDTO;
import es.redmic.models.es.administrative.model.Organisation;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class PlatformMapperTest extends MapperTestUtil {

	@Mock
	PlatformTypeESService platformTypeESService;

	@Mock
	OrganisationESService organisationESService;

	@Mock
	ContactESService contactESService;

	@Mock
	ContactRoleESService contactRoleESService;

	@InjectMocks
	PlatformESMapper mapper;

	String modelOutPath = "/data/administrative/platform/model/platform.json",
			dtoInPath = "/data/administrative/platform/dto/platform.json",
			platformTypeModel = "/data/maintenance/model/domain.json",
			organisationModel = "/data/administrative/organisation/model/organisation.json",
			contactModel = "/data/administrative/contact/model/contact.json",
			roleModel = "/data/maintenance/model/domain.json";

	@Before
	public void setupTest() throws IOException {

		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);

		DomainES platformType = (DomainES) getBean(platformTypeModel, DomainES.class);
		when(platformTypeESService.findById(anyString())).thenReturn(platformType);

		Organisation organisation = (Organisation) getBean(organisationModel, Organisation.class);
		when(organisationESService.findById(anyString())).thenReturn(organisation);

		// Contact contact = (Contact) getBean(contactModel, Contact.class);
		// when(contactESService.findById(anyString())).thenReturn(contact);

		// DomainES role = (DomainES) getBean(roleModel, DomainES.class);
		// when(contactRoleESService.findById(anyString())).thenReturn(role);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, PlatformDTO.class, Platform.class);
	}
}
