package es.redmic.test.unit.data.atlas;

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

import es.redmic.es.administrative.repository.ActivityESRepository;
import es.redmic.es.atlas.mapper.LayerESMapper;
import es.redmic.es.atlas.service.LayerESService;
import es.redmic.es.atlas.service.ThemeInspireESService;
import es.redmic.es.data.common.mapper.DataCollectionMapper;
import es.redmic.es.data.common.mapper.DataItemMapper;
import es.redmic.models.es.atlas.dto.LayerDTO;
import es.redmic.models.es.atlas.model.LayerModel;
import es.redmic.models.es.atlas.model.ThemeInspire;
import es.redmic.test.unit.geodata.common.MapperTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class LayerMapperTest extends MapperTestUtil {

	@Mock
	LayerESService layerESService;

	@Mock
	ActivityESRepository activityESRepository;

	@Mock
	ThemeInspireESService themeInspireESService;

	@InjectMocks
	LayerESMapper mapper;

	String modelOutPath = "/data/atlas/layer/model/layer.json", dtoInPath = "/data/atlas/layer/dto/layer.json",
			activitiesPath = "/data/atlas/layer/model/activities.json",
			themeInspireModel = "/data/atlas/layer/model/themeInspire.json";

	@Before
	public void setupTest() throws IOException {

		factory.addMapper(mapper);

		factory.addMapper(new DataCollectionMapper());
		factory.addMapper(new DataItemMapper());

		// when(layerESService.get(anyString())).thenReturn(null);

		ThemeInspire themeInspire = (ThemeInspire) getBean(themeInspireModel, ThemeInspire.class);
		when(themeInspireESService.findById(anyString())).thenReturn(themeInspire);

		/*
		 * JavaType type =
		 * jacksonMapper.getTypeFactory().constructParametricType(DataHitsWrapper.class,
		 * Activity.class); DataHitsWrapper activities = (DataHitsWrapper<?>)
		 * getBean(activitiesPath, type);
		 * when(activityESRepository.mget(any())).thenReturn(activities);
		 */
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, LayerDTO.class, LayerModel.class);
	}
}
