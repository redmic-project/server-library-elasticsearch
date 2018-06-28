package es.redmic.test.unit.series.timeseries;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.reflect.Whitebox;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.es.series.timeseries.converter.DirectionDatesConverter;
import es.redmic.es.series.timeseries.converter.WindroseSectorConverter;
import es.redmic.es.series.timeseries.repository.TimeSeriesESRepository;
import es.redmic.es.series.timeseries.service.TimeSeriesESService;
import es.redmic.exception.elasticsearch.ESTermQueryException;
import es.redmic.models.es.common.dto.LimitsDTO;
import es.redmic.models.es.common.query.dto.AggsPropertiesDTO;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.DateLimitsDTO;
import es.redmic.models.es.geojson.common.model.Aggregations;
import es.redmic.models.es.series.common.model.SeriesSearchWrapper;
import es.redmic.models.es.series.timeseries.model.TimeSeries;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import es.redmic.test.utils.OrikaScanBeanTest;

@RunWith(MockitoJUnitRunner.class)
public class WindroseDataTest extends MapperTestUtil {

	OrikaScanBeanTest orikaMapper = new OrikaScanBeanTest();

	@Mock
	TimeSeriesESRepository repository;

	@InjectMocks
	TimeSeriesESService service;

	String timeSeriesStats = "/series/timeseries/model/timeSeriesStats.json",
			datesByDirection = "/series/timeseries/model/datesByDirection.json",
			elementsByDates = "/series/timeseries/model/elementsByDates.json",
			successResultDTO = "/series/timeseries/dto/windroseData.json";

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {

		MemberModifier.field(TimeSeriesESService.class, "orikaMapper").set(service, orikaMapper);

		orikaMapper.addConverter(new DirectionDatesConverter());
		orikaMapper.addConverter(new WindroseSectorConverter());

		Aggregations timeSeriesStatsAggs = (Aggregations) getBean(timeSeriesStats, Aggregations.class);

		when(repository.getStatAggs(any(), any())).thenReturn(timeSeriesStatsAggs);

		Aggregations datesByDirectionAggs = (Aggregations) getBean(datesByDirection, Aggregations.class);
		when(repository.getDatesByDirectionAggs(any(), any(), any())).thenReturn(datesByDirectionAggs);

		TypeReference<List<SeriesSearchWrapper<TimeSeries>>> typeList = new TypeReference<List<SeriesSearchWrapper<TimeSeries>>>() {
		};

		List<SeriesSearchWrapper<?>> elementsByDatesWrapper = (List<SeriesSearchWrapper<?>>) getBean(elementsByDates,
				typeList);
		when(repository.getWindroseData(any(), any(), any())).thenReturn(elementsByDatesWrapper);
	}

	@Test
	public void getWindRoseData_ReturnData_IfQueryIsValid() throws Exception {

		DataQueryDTO query = getInitialQuery();

		Object windroseDataResult = Whitebox.invokeMethod(service, "getWindRoseData", query);

		JSONAssert.assertEquals(jacksonMapper.writeValueAsString(windroseDataResult), getJsonString(successResultDTO),
				false);
	}

	@Test(expected = ESTermQueryException.class)
	public void checkValidNumSector_ThrowsException_IfIsInvalid() throws Exception {

		Whitebox.invokeMethod(service, "checkValidNumSectors", 1);
		Whitebox.invokeMethod(service, "checkValidNumSectors", 3);
		Whitebox.invokeMethod(service, "checkValidNumSectors", 5);
		Whitebox.invokeMethod(service, "checkValidNumSectors", 7);
		Whitebox.invokeMethod(service, "checkValidNumSectors", 9);
	}

	@Test
	public void checkValidNumSector_NoAction_IfIsValid() throws Exception {

		Whitebox.invokeMethod(service, "checkValidNumSectors", 2);
		Whitebox.invokeMethod(service, "checkValidNumSectors", 4);
		Whitebox.invokeMethod(service, "checkValidNumSectors", 8);
		Whitebox.invokeMethod(service, "checkValidNumSectors", 16);
		Whitebox.invokeMethod(service, "checkValidNumSectors", 32);
	}

	@Test(expected = ESTermQueryException.class)
	public void getDataLimits_ThrowsException_IfRangesAreInvalid() throws Exception {

		Whitebox.invokeMethod(service, "getDataLimits", 5.0, 1.0, 4);
		Whitebox.invokeMethod(service, "getDataLimits", 1.0, 5.0, 0);
		Whitebox.invokeMethod(service, "getDataLimits", 1.0, 1.0, 2);
	}

	@Test
	public void getDataLimits_ReturnLimits_IfRangesAreValid() throws Exception {

		List<LimitsDTO> limits = Whitebox.invokeMethod(service, "getDataLimits", 1.0, 10.0, 2);
		assertEquals(limits.size(), 2);
		assertEquals(limits.get(0).getMin(), new Double(1.0));
		assertEquals(limits.get(0).getMax(), new Double(5.5));
		assertEquals(limits.get(1).getMin(), new Double(5.5));
		assertEquals(limits.get(1).getMax(), new Double(10.0));
	}

	@SuppressWarnings("unchecked")
	private DataQueryDTO getInitialQuery() throws JsonParseException, JsonMappingException, IOException {
		DataQueryDTO query = new DataQueryDTO();
		query.addAgg(new AggsPropertiesDTO());
		query.setDateLimits(jacksonMapper.readValue(
				"{\"startDate\":\"2013-04-28T04:43:00.000Z\",\"endDate\":\"2018-04-28T06:22:00.000Z\"}",
				DateLimitsDTO.class));
		query.setTerms(jacksonMapper.readValue(
				"{\"dataDefinition\": { \"speed\": 111, \"direction\": 112 }, \"numSectors\": 4, \"numSplits\": 2 }",
				Map.class));
		return query;
	}
}
