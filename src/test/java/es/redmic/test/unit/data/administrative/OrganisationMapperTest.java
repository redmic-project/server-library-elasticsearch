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

import es.redmic.es.administrative.mapper.OrganisationESMapper;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.maintenance.domain.administrative.service.CountryESService;
import es.redmic.es.maintenance.domain.administrative.service.OrganisationTypeESService;
import es.redmic.models.es.administrative.dto.OrganisationDTO;
import es.redmic.models.es.administrative.model.Organisation;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.model.Country;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class OrganisationMapperTest extends MapperTestUtil {

	@Mock
	OrganisationTypeESService organisationTypeESService;

	@Mock
	CountryESService countryESService;

	@InjectMocks
	OrganisationESMapper mapper;

	String modelOutPath = "/data/administrative/organisation/model/organisation.json",
			dtoInPath = "/data/administrative/organisation/dto/organisation.json",
			organisationTypeModel = "/data/maintenance/model/domain.json",
			countryModel = "/data/maintenance/country/model/country.json";

	@Before
	public void setupTest() throws IOException {

		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);

		DomainES organisationType = (DomainES) getBean(organisationTypeModel, DomainES.class);
		when(organisationTypeESService.findById(anyString())).thenReturn(organisationType);

		Country country = (Country) getBean(countryModel, Country.class);
		when(countryESService.findById(anyString())).thenReturn(country);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, OrganisationDTO.class, Organisation.class);
	}
}
