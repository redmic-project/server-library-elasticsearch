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

import static org.mockito.Matchers.anyString;
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

import es.redmic.es.administrative.mapper.ActivityBaseESMapper;
import es.redmic.es.administrative.service.ContactESService;
import es.redmic.es.administrative.service.DocumentESService;
import es.redmic.es.administrative.service.OrganisationESService;
import es.redmic.es.administrative.service.PlatformESService;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.maintenance.domain.administrative.mapper.ActivityDocumentESMapper;
import es.redmic.es.maintenance.domain.administrative.mapper.ContactOrganisationRoleESMapper;
import es.redmic.es.maintenance.domain.administrative.mapper.OrganisationRoleESMapper;
import es.redmic.es.maintenance.domain.administrative.mapper.PlatformContactRoleESMapper;
import es.redmic.es.maintenance.domain.administrative.service.AccessibilityESService;
import es.redmic.es.maintenance.domain.administrative.service.ContactRoleESService;
import es.redmic.es.maintenance.domain.administrative.service.OrganisationRoleESService;
import es.redmic.es.maintenance.domain.administrative.service.ScopeESService;
import es.redmic.models.es.administrative.dto.ProgramDTO;
import es.redmic.models.es.administrative.model.Contact;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.administrative.model.Organisation;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.administrative.model.Program;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class ProgramMapperTest extends MapperTestUtil {

	@Mock
	AccessibilityESService accessibilityESService;

	@Mock
	ScopeESService scopeESService;

	@Mock
	OrganisationESService organisationESService;

	@Mock
	ContactESService contactESService;

	@Mock
	ContactRoleESService contactRoleESService;

	@Mock
	OrganisationRoleESService organisationRoleESService;

	@Mock
	PlatformESService platformESService;

	@Mock
	DocumentESService documentESService;

	@InjectMocks
	ActivityBaseESMapper<Program, ProgramDTO> mapper;

	@InjectMocks
	ContactOrganisationRoleESMapper contactOrganisationRoleESMapper;

	@InjectMocks
	OrganisationRoleESMapper organisationRoleESMapper;

	@InjectMocks
	PlatformContactRoleESMapper platformContactRoleESMapper;

	@InjectMocks
	ActivityDocumentESMapper activityDocumentESMapper;

	String modelOutPath = "/data/administrative/program/model/program.json",
			dtoInPath = "/data/administrative/program/dto/program.json",
			domainModel = "/data/maintenance/model/domain.json",
			organisationModel = "/data/administrative/organisation/model/organisation.json",
			contactModel = "/data/administrative/contact/model/contact.json",
			platformModel = "/data/administrative/platform/model/platform.json",
			documentModel = "/data/administrative/document/model/document.json";

	@Before
	public void setupTest() throws IOException {

		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);
		factory.addMapper(activityDocumentESMapper);
		factory.addMapper(platformContactRoleESMapper);
		factory.addMapper(contactOrganisationRoleESMapper);
		factory.addMapper(organisationRoleESMapper);

		Organisation organisation = (Organisation) getBean(organisationModel, Organisation.class);
		when(organisationESService.findById(anyString())).thenReturn(organisation);

		Contact contact = (Contact) getBean(contactModel, Contact.class);
		when(contactESService.findById(anyString())).thenReturn(contact);

		Platform platform = (Platform) getBean(platformModel, Platform.class);
		when(platformESService.findById(anyString())).thenReturn(platform);

		Document document = (Document) getBean(documentModel, Document.class);
		when(documentESService.findById(anyString())).thenReturn(document);

		DomainES domain = (DomainES) getBean(domainModel, DomainES.class);
		when(accessibilityESService.findById(anyString())).thenReturn(domain);
		when(scopeESService.findById(anyString())).thenReturn(domain);
		when(contactRoleESService.findById(anyString())).thenReturn(domain);
		when(organisationRoleESService.findById(anyString())).thenReturn(domain);

	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, ProgramDTO.class, Program.class);
	}
}
