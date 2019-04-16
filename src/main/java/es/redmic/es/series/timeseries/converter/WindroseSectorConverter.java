package es.redmic.es.series.timeseries.converter;

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

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.ElasticSearchUtils;
import es.redmic.models.es.geojson.common.model.Aggregations;
import es.redmic.models.es.series.timeseries.dto.WindroseItemDTO;
import es.redmic.models.es.series.timeseries.dto.WindroseSectorDTO;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

@Component
public class WindroseSectorConverter extends CustomConverter<Aggregations, WindroseSectorDTO> {

	@SuppressWarnings("unchecked")
	@Override
	public WindroseSectorDTO convert(Aggregations source, Type<? extends WindroseSectorDTO> destinationType) {

		WindroseSectorDTO result = new WindroseSectorDTO();

		Map<String, Object> aggregations = source.getAttributes();
		if (aggregations == null || aggregations.size() == 0)
			return result;

		List<Map<String, Object>> sector = ElasticSearchUtils.getBucketsFromAggregations(aggregations);

		for (int i = 0; i < sector.size(); i++) {

			Integer count = (Integer) ((Map<String, Object>) sector.get(i).get("count")).get("value");
			result.add(new WindroseItemDTO(count));
		}
		return result;
	}
}
