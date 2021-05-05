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

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import es.redmic.es.series.objectcollecting.converter.ClassificationConverterBase;
import es.redmic.models.es.geojson.common.model.Aggregations;
import es.redmic.models.es.series.timeseries.dto.DataHistogramDTO;
import es.redmic.models.es.series.timeseries.dto.DataHistogramItemDTO;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

@Component
public class DataHistogramConverter extends ClassificationConverterBase<Aggregations, DataHistogramDTO> {

	@SuppressWarnings("unchecked")
	@Override
	public DataHistogramDTO convert(Aggregations source, Type<? extends DataHistogramDTO> destinationType,
		MappingContext mappingContext) {

		DataHistogramDTO data = new DataHistogramDTO();
		Map<String, Object> aggregations = source.getAttributes();
		if (aggregations == null || aggregations.size() == 0)
			return data;

		Map<String, Object> dateHistogram = (Map<String, Object>) aggregations.get("dateHistogram");
		if (dateHistogram == null || dateHistogram.size() == 0)
			return data;

		List<Map<String, Object>> hits = (List<Map<String, Object>>) dateHistogram.get("buckets");
		int size = hits.size();

		DataHistogramItemDTO lastItem = new DataHistogramItemDTO();
		for (int i = 0; i < size; i++) {
			DataHistogramItemDTO item = mapperFacade.convert(hits.get(i), DataHistogramItemDTO.class, null, null);
			if (canInsertItem(lastItem, item)) {
				data.setItemData(item);
				lastItem = item;
			}
		}
		return data;
	}

	protected static boolean canInsertItem(DataHistogramItemDTO lastItem, DataHistogramItemDTO newItem) {

		if ((newItem.hasData()) || (lastItem.hasData() && !newItem.hasData())) {
			return true;
		}
		return false;
	}
}
