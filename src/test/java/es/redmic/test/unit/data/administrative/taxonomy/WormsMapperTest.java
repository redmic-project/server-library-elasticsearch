package es.redmic.test.unit.data.administrative.taxonomy;

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

import es.redmic.es.administrative.taxonomy.mapper.TaxonESMapper;
import es.redmic.es.administrative.taxonomy.mapper.WormsESMapper;
import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.StatusESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.TaxonRankESService;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.dto.WormsDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class WormsMapperTest extends MapperTestUtil {

	@Mock
	StatusESService statusESService;

	@Mock
	TaxonRankESService taxonRankESService;

	@Mock
	TaxonESService baseESService;

	@InjectMocks
	TaxonESMapper taxonESMapper;

	@InjectMocks
	WormsESMapper mapper;

	String dtoWormsPath = "/data/administrative/taxonomy/taxon/dto/worms.json",
			dtoPath = "/data/administrative/taxonomy/taxon/dto/wormsToTaxonDTO.json",
			domainModel = "/data/maintenance/model/domain.json";

	@Before
	public void setupTest() throws IOException {

		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(taxonESMapper);
		factory.addMapper(mapper);

		DomainES domain = (DomainES) getBean(domainModel, DomainES.class);
		when(taxonRankESService.findByName_en(anyString())).thenReturn(domain);
		when(statusESService.findByName_en(anyString())).thenReturn(domain);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoWormsPath, dtoPath, WormsDTO.class, TaxonDTO.class);
	}
}
