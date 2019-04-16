package es.redmic.test.unit.series.es2dto;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.JavaType;

import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.series.common.model.SeriesSearchWrapper;
import es.redmic.models.es.series.objectcollecting.dto.ObjectCollectingSeriesDTO;
import es.redmic.models.es.series.objectcollecting.model.ObjectCollectingSeries;
import es.redmic.models.es.series.timeseries.dto.TimeSeriesDTO;
import es.redmic.models.es.series.timeseries.model.TimeSeries;
import es.redmic.test.unit.geodata.common.JsonToBeanTestUtil;
import es.redmic.test.utils.ConfigMapper;
import es.redmic.test.utils.OrikaScanBeanTest;
import ma.glasnost.orika.MappingContext;

public abstract class SeriesDataTestUtil extends JsonToBeanTestUtil {

	protected OrikaScanBeanTest factory = new OrikaScanBeanTest();

	private ConfigMapper configTest;

	public SeriesDataTestUtil(ConfigMapper configTest) throws IOException {
		this.configTest = configTest;
	}

	@Parameters
	public static Collection<ConfigMapper> data() {
		Collection<ConfigMapper> config = new ArrayList<ConfigMapper>();
		
		config.add(new ConfigMapper()
				.setDataIn("/geodata/objectcollecting/model/searchWrapperObjectCollectingSeriesModel.json")
				.setDataOut("/geodata/objectcollecting/dto/searchWrapperObjectCollectingSeriesDTO.json")
				.setOutClass(ObjectCollectingSeriesDTO.class)
				.setInClass(ObjectCollectingSeries.class));

		config.add(new ConfigMapper()
				.setDataIn("/geodata/timeseries/model/searchWrapperTimeSeriesModel.json")
				.setDataOut("/geodata/timeseries/dto/searchWrapperTimeSeriesDTO.json")
				.setOutClass(TimeSeriesDTO.class)
				.setInClass(TimeSeries.class));
		
		return config;
	}

	@Test
	public void es2dtoTest() throws IOException, ClassNotFoundException, JSONException {
	
		JavaType type = jacksonMapper.getTypeFactory().constructParametricType(SeriesSearchWrapper.class, configTest.getInClass());
		
		SeriesSearchWrapper<?> beanIn = (SeriesSearchWrapper<?>) getBean(configTest.getDataIn(), type);
		String expected = getJsonString(configTest.getDataOut());

		Map<Object, Object> globalProperties = new HashMap<Object, Object>();
		globalProperties.put("targetTypeDto", configTest.getOutClass());
		MappingContext context = new MappingContext(globalProperties);
		
		Object beanOut = factory.getMapperFacade().map(beanIn.getHits(), JSONCollectionDTO.class, context);

		String result = jacksonMapper.writeValueAsString(beanOut);
		
		JSONAssert.assertEquals(result, expected, true);
	}
}
