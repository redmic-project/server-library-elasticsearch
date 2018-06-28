package es.redmic.test.unit.series.timeseries;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.models.es.series.timeseries.dto.TimeSeriesDTO;
import es.redmic.models.es.series.timeseries.model.TimeSeries;
import es.redmic.test.unit.geodata.common.MapperTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class TimeSeriesMapperTest extends MapperTestUtil {

	String modelOutPath = "/geodata/timeseries/model/timeSeries.json",
			dtoInPath = "/geodata/timeseries/dto/timeSeriesIn.json";

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, TimeSeriesDTO.class, TimeSeries.class);
	}
}
