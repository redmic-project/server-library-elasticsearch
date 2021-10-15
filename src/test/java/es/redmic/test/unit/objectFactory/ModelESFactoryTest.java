package es.redmic.test.unit.objectFactory;

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

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.administrative.taxonomy.model.TaxonValid;
import es.redmic.models.es.common.model.BaseES;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.TypeFactory;

/*
 *
 * Test de ModelESFactory que es una componente genérica que pasándole un servicio y una clase
 * hace un get a elastic del item y rellena el modelo.
 *
 * Se testea un caso específico; rellenar un modelo TaxonValid. Los demás casos son equivalentes por
 * tanto, se dan por testeados.
 *
 * */
@RunWith(MockitoJUnitRunner.class)
public class ModelESFactoryTest {

	@Mock
	TaxonESService service;

	@InjectMocks
	ModelESFactory objectFactory;

	ObjectMapper jacksonMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JtsModule());

	TaxonValid taxonExpected;

	protected MapperFactory factory = new DefaultMapperFactory.Builder().build();

	@Before
	public void setupTest() {
		MockitoAnnotations.initMocks(this);
		factory.registerObjectFactory(objectFactory, TypeFactory.<BaseES<?>>valueOf(BaseES.class));

		Taxon taxon = new Taxon();
		taxon.setId(1L);
		taxon.setScientificName("taxonPrueba");

		when(service.findById(any(String.class))).thenReturn(taxon);

		taxonExpected = factory.getMapperFacade().map(taxon, TaxonValid.class);
	}

	@Test
	public void completeTaxonValidModel() throws JSONException, JsonProcessingException {

		TaxonDTO taxonDTO = new TaxonDTO();
		taxonDTO.setId(1L);

		MappingContext.Factory mappingContextFactory = new MappingContext.Factory();
		MappingContext context = mappingContextFactory.getContext();
		context.setProperty("service", service);

		TaxonValid taxonValid = (TaxonValid) factory.getMapperFacade().map(
				factory.getMapperFacade().newObject(taxonDTO, TypeFactory.<BaseES<?>>valueOf(BaseES.class), context),
				TaxonValid.class);

		JSONAssert.assertEquals(jacksonMapper.writeValueAsString(taxonExpected), jacksonMapper.writeValueAsString(taxonValid), true);
	}
}
