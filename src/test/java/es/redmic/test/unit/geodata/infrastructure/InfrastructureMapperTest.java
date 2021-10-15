package es.redmic.test.unit.geodata.infrastructure;

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

import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.geodata.infrastructure.converter.InfrastructureTypeClassificationESConverter;
import es.redmic.es.geodata.infrastructure.mapper.InfrastructureESMapper;
import es.redmic.es.geodata.infrastructure.mapper.InfrastructurePropertiesESMapper;
import es.redmic.es.maintenance.point.service.InfrastructureTypeESService;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.infrastructure.dto.InfrastructureDTO;
import es.redmic.models.es.maintenance.point.model.InfrastructureType;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureMapperTest extends MapperTestUtil {

	@Mock
	InfrastructureTypeESService infrastructureTypeESService;

	@InjectMocks
	InfrastructureTypeClassificationESConverter converter;

	@InjectMocks
	InfrastructureESMapper featureMapper;

	@InjectMocks
	InfrastructurePropertiesESMapper mapper;

	String modelOutPath = "/geodata/infrastructure/model/infrastructure.json",
			dtoInPath = "/geodata/infrastructure/dto/infrastructureIn.json",
			classification1 = "/geodata/infrastructure/model/classification1.json",
			classification2 = "/geodata/infrastructure/model/classification2.json";

	@Before
	public void setupTest() throws IOException {

		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);
		factory.addMapper(featureMapper);
		factory.addConverter(converter);

		InfrastructureType infrastructureType1 = (InfrastructureType) getBean(classification1,
				InfrastructureType.class),
				infrastructureType2 = (InfrastructureType) getBean(classification2, InfrastructureType.class);
		when(infrastructureTypeESService.findById("1")).thenReturn(infrastructureType1);
		when(infrastructureTypeESService.findById("2")).thenReturn(infrastructureType2);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, InfrastructureDTO.class, GeoPointData.class);
	}
}
