package es.redmic.es.series.common.converter;

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

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import es.redmic.models.es.series.timeseries.dto.DataHistogramStatsDTO;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

@SuppressWarnings("rawtypes")
@Component
public class ItemStatsHistogramConverter extends CustomConverter<LinkedHashMap, DataHistogramStatsDTO> {

	@Override
	public DataHistogramStatsDTO convert(LinkedHashMap source, Type<? extends DataHistogramStatsDTO> destinationType) {
		DataHistogramStatsDTO item = new DataHistogramStatsDTO();
		item.setAvg((Double) source.get("avg"));
		item.setCount((Integer) source.get("count"));
		item.setMax((Double) source.get("max"));
		item.setMin((Double) source.get("min"));
		item.setSum((Double) source.get("sum"));
		return item;
	}
}
