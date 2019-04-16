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
