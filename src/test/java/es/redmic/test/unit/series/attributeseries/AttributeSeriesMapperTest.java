package es.redmic.test.unit.series.attributeseries;

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

import es.redmic.es.maintenance.qualifiers.service.AttributeTypeESService;
import es.redmic.es.series.attributeseries.mapper.AttributeSeriesESMapper;
import es.redmic.models.es.maintenance.qualifiers.model.AttributeType;
import es.redmic.models.es.series.attributeseries.dto.AttributeSeriesDTO;
import es.redmic.models.es.series.attributeseries.model.AttributeSeries;
import es.redmic.test.unit.geodata.common.MapperTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class AttributeSeriesMapperTest extends MapperTestUtil {

	String modelOutPath = "/series/attributeseries/model/attributeSeries.json",
			dtoInPath = "/series/attributeseries/dto/attributeSeriesIn.json",
			attributeTypePath10 = "/data/maintenance/attributetype/model/attributetypebase10.json",
			attributeTypePath11 = "/data/maintenance/attributetype/model/attributetypebase11.json";

	@Mock
	AttributeTypeESService attributeTypeESService;

	@InjectMocks
	AttributeSeriesESMapper mapper;

	@Before
	public void setupTest() throws IOException {

		factory.addMapper(mapper);

		AttributeType attributeType10 = (AttributeType) getBean(attributeTypePath10,
				AttributeType.class)/*
									 * , attributeType11 = (AttributeType) getBean(attributeTypePath11,
									 * AttributeType.class)
									 */;

		when(attributeTypeESService.findById("10")).thenReturn(attributeType10);
		// when(attributeTypeESService.findById("11")).thenReturn(attributeType11);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, AttributeSeriesDTO.class, AttributeSeries.class);
	}
}
