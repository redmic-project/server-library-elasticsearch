package es.redmic.test.unit.series.timeseries;

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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.support.membermodification.MemberModifier;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.vividsolutions.jts.geom.Geometry;

import es.redmic.es.geodata.geofixedstation.repository.GeoFixedTimeSeriesESRepository;
import es.redmic.es.geodata.geofixedstation.service.GeoFixedTimeSeriesESService;
import es.redmic.es.series.common.utils.DataDefinitionUtils;
import es.redmic.es.series.timeseries.service.TimeSeriesESService;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.series.common.model.SeriesSearchWrapper;
import es.redmic.models.es.series.timeseries.dto.RawDataDTO;
import es.redmic.models.es.series.timeseries.model.TimeSeries;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import es.redmic.test.utils.OrikaScanBeanTest;

@RunWith(MockitoJUnitRunner.class)
public class TimeSeriesESServiceTest extends MapperTestUtil {

	OrikaScanBeanTest orikaMapper = new OrikaScanBeanTest();

	@InjectMocks
	TimeSeriesESService service;

	@Mock
	GeoFixedTimeSeriesESRepository repositoryGeoFixed;

	@Mock
	GeoFixedTimeSeriesESService geoFixedTimeSeriesESSevice;

	@SuppressWarnings("serial")
	ArrayList<String> returnFields = new ArrayList<String>() {
		{
			add("date");
			add("value");
			add("dataDefinition");
		}
	};

	String histogramModelInPath = "/geodata/timeseries/model/histogramTimeSeries.json",
			histogramDtoOutPath = "/geodata/timeseries/dto/histogramTimeSeries.json",
			histogramModelInPathTimeIntervalLowToHigh = "/geodata/timeseries/model/histogramTimeSeriesTimeIntervalLowToHigh.json",
			histogramDtoOutPathTimeIntervalLowToHigh = "/geodata/timeseries/dto/histogramTimeSeriesOutTimeIntervalLowToHigh.json",
			histogramModelInPathTimeIntervalHighToLow = "/geodata/timeseries/model/histogramTimeSeriesTimeIntervalHighToLow.json",
			histogramDtoOutPathTimeIntervalHighToLow = "/geodata/timeseries/dto/histogramTimeSeriesOutTimeIntervalHighToLow.json",
			geoFixedTimeSeriesTimeIntervalLow = "/geodata/timeseries/model/geoFixedTimeSeriesTimeIntervalLow.json",
			geoFixedTimeSeriesTimeIntervalHigh = "/geodata/timeseries/model/geoFixedTimeSeriesTimeIntervalHigh.json";

	@Before
	public void setUp() throws Exception {

		MemberModifier.field(TimeSeriesESService.class, "orikaMapper").set(service, orikaMapper);
	}

	@SuppressWarnings("serial")
	@Test
	public void addDataDefinitionFieldToReturn() throws Exception {

		DataQueryDTO query = new DataQueryDTO();
		query.setReturnFields(new ArrayList<String>() {
			{
				add("date");
				add("value");
			}
		});

		DataDefinitionUtils.addDataDefinitionFieldToReturn(query);
		assertEquals(query.getReturnFields().size(), 3);
		Assert.assertEquals(query.getReturnFields(), returnFields);
	}

	@Test
	public void addDataDefinitionFieldToReturnWhenArrayIsEmpty() throws Exception {

		DataQueryDTO query = new DataQueryDTO();
		DataDefinitionUtils.addDataDefinitionFieldToReturn(query);
		assertEquals(query.getReturnFields().size(), 3);
		Assert.assertEquals(query.getReturnFields(), returnFields);
	}

	/**
	 * Test para comprobar si en caso de que el siguiente item llegue dentro de
	 * los límites esperados no se introduzca un valor nulo en la serie.
	 * 
	 * @throws Exception
	 */
	@Test
	public void fulfillIntervalCheckPass() throws Exception {
		Long itemInterval = 3600000L;
		int tolerance = new Double(itemInterval * 0.4).intValue();
		DateTime currentDatetime = new DateTime();
		DateTime nextDatetime = new DateTime().plusMillis((int) (itemInterval + tolerance));
		Object result = DataDefinitionUtils.fulfillIntervalCheck(currentDatetime, nextDatetime, itemInterval);
		assertEquals(result, true);
	}

	/**
	 * Test para comprobar si en caso de que el siguiente item llegue después de
	 * lo esperado meta un nulo en la serie en el momento del dato esperado.
	 * 
	 * @throws Exception
	 */
	@Test
	public void fulfillIntervalCheckNoPassWhenTimeSeriesHasPositiveOffset() throws Exception {
		Long itemIntervalInSeconds = 3600L;
		DateTime currentDatetime = new DateTime(2013, 11, 6, 22, 30); // 24 min
																		// de
																		// tolerancia
		DateTime nextDatetime = new DateTime(2013, 11, 6, 23, 55);
		Object result = DataDefinitionUtils.fulfillIntervalCheck(currentDatetime, nextDatetime, itemIntervalInSeconds);
		assertEquals(result, false);
	}

	/**
	 * Test para comprobar si en caso de que el siguiente item llegue antes de
	 * lo esperado no meta un nulo en la serie.
	 * 
	 * @throws Exception
	 */
	@Test
	public void fulfillIntervalCheckPassWhenHasNegativeOffset() throws Exception {
		Long itemInterval = 3600000L;
		DateTime currentDatetime = new DateTime();
		DateTime nextDatetime = new DateTime().plusSeconds((int) (itemInterval / 2000));
		Object result = DataDefinitionUtils.fulfillIntervalCheck(currentDatetime, nextDatetime, itemInterval);
		assertEquals(result, true);
	}

	@Test
	public void checkTimeSeriesDataWithGap() throws IOException, JSONException {

		loadGeoFixedTimeSeries(geoFixedTimeSeriesTimeIntervalLow, 9L);

		String dtoStringExpected = getJsonString(histogramDtoOutPath);

		List<Integer> dataDefinitionIds = new ArrayList<Integer>();
		dataDefinitionIds.add(9);

		RawDataDTO result = service.getSourceToResult(getResults(histogramModelInPath), dataDefinitionIds);

		JSONAssert.assertEquals(jacksonMapper.writeValueAsString(result), dtoStringExpected, false);
	}

	@Test(expected = InternalException.class)
	public void checkTimeSeriesDataWithTimeIntervalIsNullReturnInternalException() throws IOException {

		GeoSearchWrapper<GeoDataProperties, Geometry> geoDD9 = loadGeoFixedTimeSeries(geoFixedTimeSeriesTimeIntervalLow,
				9L);
		geoDD9.getSource(0).getProperties().getMeasurements().get(0).getDataDefinition().setTimeInterval(null);
		when(geoFixedTimeSeriesESSevice.findByDataDefinition(9L)).thenReturn(geoDD9);

		List<Integer> dataDefinitionIds = new ArrayList<Integer>();
		dataDefinitionIds.add(9);

		service.getSourceToResult(getResults(histogramModelInPath), dataDefinitionIds);
	}

	@Test(expected = InternalException.class)
	public void checkTimeSeriesDataWithDifferentPathReturnInternalException() throws IOException {

		GeoSearchWrapper<GeoDataProperties, Geometry> timeIntervalLow = loadGeoFixedTimeSeries(
				geoFixedTimeSeriesTimeIntervalLow, 9L);
		timeIntervalLow.getSource(0).getProperties().getMeasurements().get(0).getDataDefinition().setPath("r.1.1.1");
		loadGeoFixedTimeSeries(geoFixedTimeSeriesTimeIntervalHigh, 10L);

		List<Integer> dataDefinitionIds = new ArrayList<Integer>();
		dataDefinitionIds.add(9);
		dataDefinitionIds.add(10);

		service.getSourceToResult(getResults(histogramModelInPath), dataDefinitionIds);
	}

	@Test(expected = InternalException.class)
	public void checkTimeSeriesDataWithDifferentZReturnInternalException() throws IOException {

		GeoSearchWrapper<GeoDataProperties, Geometry> timeIntervalLow = loadGeoFixedTimeSeries(
				geoFixedTimeSeriesTimeIntervalLow, 9L);
		timeIntervalLow.getSource(0).getProperties().getMeasurements().get(0).getDataDefinition().setZ(-20.0);
		loadGeoFixedTimeSeries(geoFixedTimeSeriesTimeIntervalHigh, 10L);

		List<Integer> dataDefinitionIds = new ArrayList<Integer>();
		dataDefinitionIds.add(9);
		dataDefinitionIds.add(10);

		service.getSourceToResult(getResults(histogramModelInPath), dataDefinitionIds);
	}

	@Test
	public void checkTimeSeriesDataWhenFirstTimeIntervalIsLessThanSecond() throws IOException, JSONException {

		loadGeoFixedTimeSeries(geoFixedTimeSeriesTimeIntervalLow, 9L);
		loadGeoFixedTimeSeries(geoFixedTimeSeriesTimeIntervalHigh, 10L);

		String dtoStringExpected = getJsonString(histogramDtoOutPathTimeIntervalLowToHigh);

		List<Integer> dataDefinitionIds = new ArrayList<Integer>();
		dataDefinitionIds.add(9);
		dataDefinitionIds.add(10);

		RawDataDTO result = service.getSourceToResult(getResults(histogramModelInPathTimeIntervalLowToHigh),
				dataDefinitionIds);

		JSONAssert.assertEquals(jacksonMapper.writeValueAsString(result), dtoStringExpected, false);
	}

	@Test
	public void checkTimeSeriesDataWhenSecondTimeIntervalIsLessThanFirst() throws IOException, JSONException {

		loadGeoFixedTimeSeries(geoFixedTimeSeriesTimeIntervalLow, 9L);
		loadGeoFixedTimeSeries(geoFixedTimeSeriesTimeIntervalHigh, 10L);

		String dtoStringExpected = getJsonString(histogramDtoOutPathTimeIntervalHighToLow);

		List<Integer> dataDefinitionIds = new ArrayList<Integer>();
		dataDefinitionIds.add(10);
		dataDefinitionIds.add(9);

		RawDataDTO result = service.getSourceToResult(getResults(histogramModelInPathTimeIntervalHighToLow),
				dataDefinitionIds);

		JSONAssert.assertEquals(jacksonMapper.writeValueAsString(result), dtoStringExpected, false);
	}

	/**
	 * Util para cargar bean
	 */
	@SuppressWarnings("unchecked")
	private GeoSearchWrapper<GeoDataProperties, Geometry> loadGeoFixedTimeSeries(String path, Long id)
			throws IOException {

		TypeReference<GeoSearchWrapper<GeoDataProperties, Geometry>> typeRef = new TypeReference<GeoSearchWrapper<GeoDataProperties, Geometry>>() {
		};
		GeoSearchWrapper<GeoDataProperties, Geometry> geo = (GeoSearchWrapper<GeoDataProperties, Geometry>) getBean(
				path, typeRef);
		when(geoFixedTimeSeriesESSevice.findByDataDefinition(id)).thenReturn(geo);
		return geo;
	}

	private SeriesSearchWrapper<TimeSeries> getResults(String path)
			throws JsonParseException, JsonMappingException, IOException {

		TypeReference<SeriesSearchWrapper<TimeSeries>> typeRef = new TypeReference<SeriesSearchWrapper<TimeSeries>>() {
		};
		return jacksonMapper.readValue(getJsonString(path), typeRef);
	}
}
