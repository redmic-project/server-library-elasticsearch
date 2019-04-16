package es.redmic.test.unit.geodata.citation;

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
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.es.administrative.taxonomy.service.MisidentificationESService;
import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.geodata.citation.mapper.CitationESMapper;
import es.redmic.es.geodata.citation.mapper.CitationPropertiesESMapper;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.ConfidenceESService;
import es.redmic.models.es.administrative.taxonomy.model.Misidentification;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.geojson.citation.dto.CitationDTO;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class CitationMapperTest extends MapperTestUtil {

	@Mock
	TaxonESService taxonService;

	@Mock
	ConfidenceESService confidenceService;

	@Mock
	MisidentificationESService misidentificationService;

	@InjectMocks
	CitationESMapper featureMapper;

	@InjectMocks
	CitationPropertiesESMapper mapper;

	String modelOutPath = "/geodata/citation/model/citation.json", dtoInPath = "/geodata/citation/dto/citationIn.json",
			taxonModel = "/data/taxonomy/model/taxonValid.json",
			misidentificationModel = "/data/taxonomy/model/misidentification.json",
			confidenceModel = "/geodata/common/model/confidence.json";

	@Before
	public void setupTest() throws IOException {

		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);
		factory.addMapper(featureMapper);

		Taxon taxonExpected = (Taxon) getBean(taxonModel, Taxon.class);
		DomainES confidence = (DomainES) getBean(confidenceModel, DomainES.class);
		Misidentification misidentification = (Misidentification) getBean(misidentificationModel,
				Misidentification.class);

		when(taxonService.findById(any(String.class))).thenReturn(taxonExpected);
		when(confidenceService.findById(any(String.class))).thenReturn(confidence);
		when(misidentificationService.findById(any(String.class))).thenReturn(misidentification);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, CitationDTO.class, GeoPointData.class);
	}
}
