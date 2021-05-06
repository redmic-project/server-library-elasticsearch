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

import es.redmic.es.administrative.mapper.ActivityESMapper;
import es.redmic.es.administrative.mapper.ProjectESMapper;
import es.redmic.es.administrative.repository.ActivityBaseESRepository;
import es.redmic.es.administrative.service.ContactESService;
import es.redmic.es.administrative.service.DocumentESService;
import es.redmic.es.administrative.service.OrganisationESService;
import es.redmic.es.administrative.service.PlatformESService;
import es.redmic.es.administrative.service.ProgramESService;
import es.redmic.es.administrative.service.ProjectESService;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.data.common.mapper.DataCollectionMapper;
import es.redmic.es.data.common.mapper.DataItemMapper;
import es.redmic.es.maintenance.domain.administrative.mapper.ActivityDocumentESMapper;
import es.redmic.es.maintenance.domain.administrative.mapper.ContactOrganisationRoleESMapper;
import es.redmic.es.maintenance.domain.administrative.mapper.OrganisationRoleESMapper;
import es.redmic.es.maintenance.domain.administrative.mapper.PlatformContactRoleESMapper;
import es.redmic.es.maintenance.domain.administrative.service.AccessibilityESService;
import es.redmic.es.maintenance.domain.administrative.service.ActivityRankESService;
import es.redmic.es.maintenance.domain.administrative.service.ActivityTypeESService;
import es.redmic.es.maintenance.domain.administrative.service.ContactRoleESService;
import es.redmic.es.maintenance.domain.administrative.service.OrganisationRoleESService;
import es.redmic.es.maintenance.domain.administrative.service.ScopeESService;
import es.redmic.es.maintenance.domain.administrative.service.ThemeInspireESService;
import es.redmic.models.es.administrative.dto.ActivityDTO;
import es.redmic.models.es.administrative.model.Activity;
import es.redmic.models.es.administrative.model.Contact;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.administrative.model.Organisation;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.administrative.model.Program;
import es.redmic.models.es.administrative.model.Project;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.model.ActivityType;
import es.redmic.models.es.maintenance.administrative.model.ThemeInspire;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class ActivityMapperTest extends MapperTestUtil {

	@Mock
	AccessibilityESService accessibilityESService;

	@Mock
	ScopeESService scopeESService;

	@Mock
	ActivityBaseESRepository baseRepository;

	@Mock
	ProjectESService projectESService;

	@Mock
	ProgramESService programESService;

	@Mock
	ActivityTypeESService activityTypeESService;

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

	@Mock
	ActivityRankESService rankESService;

	@Mock
	ThemeInspireESService themeInspireESService;

	@InjectMocks
	ActivityESMapper mapper;

	@InjectMocks
	ContactOrganisationRoleESMapper contactOrganisationRoleESMapper;

	@InjectMocks
	OrganisationRoleESMapper organisationRoleESMapper;

	@InjectMocks
	PlatformContactRoleESMapper platformContactRoleESMapper;

	@InjectMocks
	ActivityDocumentESMapper activityDocumentESMapper;

	String modelOutPath = "/data/administrative/activity/model/activity2.json",
			dtoInPath = "/data/administrative/activity/dto/activity.json",
			projectModel = "/data/administrative/project/model/project.json",
			programModel = "/data/administrative/program/model/program.json",
			activityTypeModel = "/data/maintenance/activitytype/model/activityType.json",
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
		factory.addMapper(new DataCollectionMapper());
		factory.addMapper(new DataItemMapper());

		Project project = (Project) getBean(projectModel, Project.class);
		when(projectESService.findById(anyString())).thenReturn(project);

		Program program = (Program) getBean(programModel, Program.class);
		when(programESService.findById(anyString())).thenReturn(program);

		Organisation organisation = (Organisation) getBean(organisationModel, Organisation.class);
		when(organisationESService.findById(anyString())).thenReturn(organisation);

		Contact contact = (Contact) getBean(contactModel, Contact.class);
		when(contactESService.findById(anyString())).thenReturn(contact);

		Platform platform = (Platform) getBean(platformModel, Platform.class);
		when(platformESService.findById(anyString())).thenReturn(platform);

		Document document = (Document) getBean(documentModel, Document.class);
		when(documentESService.findById(anyString())).thenReturn(document);

		ActivityType activityType = (ActivityType) getBean(activityTypeModel, ActivityType.class);
		when(activityTypeESService.findById(anyString())).thenReturn(activityType);

		ThemeInspire themeInspire = new ThemeInspire();
		themeInspire.setId(1L);
		themeInspire.setName("name");
		themeInspire.setName_en("name_en");
		themeInspire.setCode("code");

		DomainES domain = (DomainES) getBean(domainModel, DomainES.class);
		when(accessibilityESService.findById(anyString())).thenReturn(domain);
		when(scopeESService.findById(anyString())).thenReturn(domain);
		when(contactRoleESService.findById(anyString())).thenReturn(domain);
		when(organisationRoleESService.findById(anyString())).thenReturn(domain);
		when(rankESService.findById(anyString())).thenReturn(domain);
		when(themeInspireESService.findById(anyString())).thenReturn(themeInspire);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, ActivityDTO.class, Activity.class);
	}
}
