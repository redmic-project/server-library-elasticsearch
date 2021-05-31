package es.redmic.test.unit.geodata.geofixedstation;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.es.geodata.geofixedstation.mapper.DataDefinitionESMapper;
import es.redmic.es.geodata.geofixedstation.mapper.FixedSurveySeriesPropertiesESMapper;
import es.redmic.es.geodata.geofixedstation.mapper.MeasurementESMapper;
import es.redmic.es.geodata.geofixedstation.mapper.SiteESMapper;
import es.redmic.es.maintenance.device.service.DeviceESService;
import es.redmic.es.maintenance.parameter.service.ParameterESService;
import es.redmic.es.maintenance.parameter.service.UnitESService;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.geofixedstation.dto.GeoFixedTimeSeriesDTO;
import es.redmic.models.es.maintenance.device.model.Device;
import es.redmic.models.es.maintenance.parameter.model.Parameter;
import es.redmic.models.es.maintenance.parameter.model.Unit;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.MappingContext;

@RunWith(MockitoJUnitRunner.class)
public class GeoFixedTimeSeriesMapperTest extends MapperTestUtil {

	@Mock
	ParameterESService parameterESService;

	@Mock
	UnitESService unitESService;

	@Mock
	DeviceESService deviceService;

	@InjectMocks
	DataDefinitionESMapper dataDefinitionMapper;

	@InjectMocks
	MeasurementESMapper measurementMapper;

	@InjectMocks
	SiteESMapper mapper;

	String modelOutPath = "/geodata/timeseries/model/geoFixedTimeSeries.json",
			dtoInPath = "/geodata/timeseries/dto/geoFixedTimeSeriesIn.json",
			parameterBaseModel = "/data/administrative/parameter/model/parameterBase.json",
			unitModel = "/data/administrative/parameter/model/unit.json",
			deviceCompactModel = "/data/administrative/device/model/deviceCompact.json";

	@Before
	public void setupTest() throws IOException {

		factory.addMapper(dataDefinitionMapper);
		factory.addMapper(measurementMapper);
		factory.addMapper(mapper);
		factory.addMapper(new FixedSurveySeriesPropertiesESMapper());

		Parameter parameterExpected = (Parameter) getBean(parameterBaseModel, Parameter.class);
		Unit unitExpected = (Unit) getBean(unitModel, Unit.class);
		Device deviceExpected = (Device) getBean(deviceCompactModel, Device.class);

		when(parameterESService.findById(any(String.class))).thenReturn(parameterExpected);
		when(unitESService.findById(any(String.class))).thenReturn(unitExpected);
		when(deviceService.findById(any(String.class))).thenReturn(deviceExpected);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		GeoFixedTimeSeriesDTO dtoIn = (GeoFixedTimeSeriesDTO) getBean(dtoInPath, GeoFixedTimeSeriesDTO.class);

		MappingContext context = factory.getMappingContext();
		context.setProperty("uuid", dtoIn.getUuid());
		context.setProperty("geoDataPrefix", DataPrefixType.FIXED_TIMESERIES);

		GeoPointData modelOut = factory.getMapperFacade().map(dtoIn, GeoPointData.class, context);

		String modelStringExpected = getJsonString(modelOutPath);

		JSONAssert.assertEquals(jacksonMapper.writeValueAsString(modelOut), modelStringExpected, false);
	}
}
