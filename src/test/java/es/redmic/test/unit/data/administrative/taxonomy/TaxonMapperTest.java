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

import static org.mockito.Matchers.anyString;
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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.es.administrative.taxonomy.mapper.TaxonESMapper;
import es.redmic.es.administrative.taxonomy.repository.TaxonESRepository;
import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.StatusESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.TaxonRankESService;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class TaxonMapperTest extends MapperTestUtil {
	
	@Mock
	TaxonESRepository baseESRepository;
	
	@Mock
	TaxonESService baseESService;
	
	@Mock
	StatusESService statusESService;
	
	@Mock
	TaxonRankESService taxonRankESService;
	
	@InjectMocks
	TaxonESMapper mapper;
	
	String modelOutPath = "/data/administrative/taxonomy/taxon/model/taxon.json",
			dtoInPath = "/data/administrative/taxonomy/taxon/dto/taxon.json",
			domainModel = "/data/maintenance/model/domain.json",
			taxonModel = "/data/administrative/taxonomy/taxon/model/parent.json";
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setupTest() throws IOException {
		
		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);
		
		DomainES domain = (DomainES) getBean(domainModel, DomainES.class);	
		when(taxonRankESService.findById(anyString())).thenReturn(domain);
		when(statusESService.findById(anyString())).thenReturn(domain);

		JavaType typeTaxon = jacksonMapper.getTypeFactory().constructParametricType(DataHitWrapper.class, Taxon.class);
		DataHitWrapper parent = (DataHitWrapper<?>) getBean(taxonModel, typeTaxon);
		when(baseESRepository.findById(anyString())).thenReturn(parent);
		
		when(baseESService.findById(anyString())).thenReturn((Taxon) parent.get_source());
	}
	
	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {
		
		mapperDtoToModel(dtoInPath, modelOutPath, TaxonDTO.class, Taxon.class);
	}
}
