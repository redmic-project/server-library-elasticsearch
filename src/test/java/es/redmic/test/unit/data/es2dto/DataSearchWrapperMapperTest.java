package es.redmic.test.unit.data.es2dto;

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
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.JavaType;

import es.redmic.es.administrative.mapper.ActivityBaseESMapper;
import es.redmic.es.administrative.mapper.ActivityESMapper;
import es.redmic.es.administrative.mapper.ContactESMapper;
import es.redmic.es.administrative.mapper.DocumentESMapper;
import es.redmic.es.administrative.mapper.OrganisationESMapper;
import es.redmic.es.administrative.mapper.PlatformESMapper;
import es.redmic.es.administrative.mapper.ProjectESMapper;
import es.redmic.es.administrative.service.ProgramESService;
import es.redmic.es.administrative.service.ProjectESService;
import es.redmic.es.administrative.taxonomy.mapper.AnimalESMapper;
import es.redmic.es.administrative.taxonomy.mapper.RecoveryESMapper;
import es.redmic.es.administrative.taxonomy.mapper.SpeciesESMapper;
import es.redmic.es.administrative.taxonomy.mapper.TaxonESMapper;
import es.redmic.es.administrative.taxonomy.repository.TaxonESRepository;
import es.redmic.es.data.common.mapper.DataCollectionMapper;
import es.redmic.es.data.common.mapper.DataItemMapper;
import es.redmic.es.maintenance.device.mapper.CalibrationESMapper;
import es.redmic.es.maintenance.device.mapper.DeviceESMapper;
import es.redmic.es.maintenance.domain.administrative.mapper.ActivityDocumentESMapper;
import es.redmic.es.maintenance.domain.administrative.mapper.ActivityTypeESMapper;
import es.redmic.es.maintenance.domain.administrative.mapper.ContactOrganisationRoleESMapper;
import es.redmic.es.maintenance.domain.administrative.mapper.OrganisationRoleESMapper;
import es.redmic.es.maintenance.domain.administrative.mapper.PlatformContactRoleESMapper;
import es.redmic.es.maintenance.parameter.mapper.ParameterESMapper;
import es.redmic.es.maintenance.parameter.mapper.UnitESMapper;
import es.redmic.models.es.administrative.dto.ProgramDTO;
import es.redmic.models.es.administrative.model.Program;
import es.redmic.models.es.administrative.model.Project;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.test.utils.ConfigMapper;

@RunWith(Parameterized.class)
public class DataSearchWrapperMapperTest extends DataTestUtil {

	@Mock
	ProgramESService programESService;

	@InjectMocks
	ProjectESMapper projectESMapper;

	@Mock
	ProjectESService projectESService;

	@InjectMocks
	ActivityESMapper activityESMapper;

	@Mock
	TaxonESRepository taxonESRepository;

	@InjectMocks
	TaxonESMapper taxonESMapper;

	@InjectMocks
	SpeciesESMapper speciesESMapper;

	String taxonModel = "/data/administrative/taxonomy/taxon/model/parent.json",
			projectParent = "/data/administrative/project/model/parent.json",
			activityParent = "/data/administrative/activity/model/parent.json";

	public DataSearchWrapperMapperTest(ConfigMapper configTest) throws IOException {
		super(configTest);

		// @formatter:off
		factory.addMapper(new DataCollectionMapper());
		factory.addMapper(new DataItemMapper());
		factory.addMapper(new ActivityTypeESMapper());
		factory.addMapper(new ContactOrganisationRoleESMapper());
		factory.addMapper(new OrganisationRoleESMapper());
		factory.addMapper(new PlatformContactRoleESMapper());
		factory.addMapper(new ActivityDocumentESMapper());
		factory.addMapper(new OrganisationESMapper());
		factory.addMapper(new ContactESMapper());
		factory.addMapper(new DocumentESMapper());
		factory.addMapper(new PlatformESMapper());
		factory.addMapper(new CalibrationESMapper());
		factory.addMapper(new DeviceESMapper());
		factory.addMapper(new UnitESMapper());
		factory.addMapper(new ParameterESMapper());
		factory.addMapper(new ActivityBaseESMapper<Program, ProgramDTO>());
		factory.addMapper(new RecoveryESMapper());
		factory.addMapper(new AnimalESMapper());
		// @formatter:on
	}

	/*
	 * Añade mapper especiales a los cuales hay que inyectar dependencias
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setupTest() throws IOException {

		initMocks(this);

		factory.addMapper(taxonESMapper);
		factory.addMapper(speciesESMapper);
		factory.addMapper(activityESMapper);
		factory.addMapper(projectESMapper);

		JavaType typeTaxon = jacksonMapper.getTypeFactory().constructParametricType(DataHitWrapper.class, Taxon.class);
		DataHitWrapper parent = (DataHitWrapper<?>) getBean(taxonModel, typeTaxon);
		when(taxonESRepository.findById(anyString())).thenReturn(parent);

		when(projectESService.findById(anyString())).thenReturn((Project) getBean(activityParent, Project.class));
		when(programESService.findById(anyString())).thenReturn((Program) getBean(projectParent, Program.class));
	}
}
