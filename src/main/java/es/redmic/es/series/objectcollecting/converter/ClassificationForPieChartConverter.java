package es.redmic.es.series.objectcollecting.converter;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.ElasticSearchUtils;
import es.redmic.models.es.geojson.common.model.Aggregations;
import es.redmic.models.es.series.objectcollecting.dto.ClassificationForPieChartDTO;
import es.redmic.models.es.series.objectcollecting.dto.ClassificationsForPieChartDTO;
import es.redmic.models.es.series.objectcollecting.dto.IntervalAggregationDTO;
import ma.glasnost.orika.metadata.Type;

@Component
public class ClassificationForPieChartConverter
		extends ClassificationConverterBase<Aggregations, ClassificationsForPieChartDTO> {

	@SuppressWarnings("unchecked")
	@Override
	public ClassificationsForPieChartDTO convert(Aggregations source,
			Type<? extends ClassificationsForPieChartDTO> destinationType) {

		ClassificationsForPieChartDTO classificationList = new ClassificationsForPieChartDTO();

		Map<String, Object> aggregations = source.getAttributes();
		if (aggregations == null || aggregations.size() == 0)
			return classificationList;

		List<Map<String, Object>> classificationIntervals = ElasticSearchUtils.getBucketsFromAggregations(aggregations);

		List<ClassificationForPieChartDTO> classifications = new ArrayList<ClassificationForPieChartDTO>();
		for (int i = 0; i < classificationIntervals.size(); i++) {

			String timeInterval = classificationIntervals.get(i).get("key_as_string").toString();

			List<Map<String, Object>> types = ElasticSearchUtils
					.getBucketsFromAggregations((Map<String, Object>) classificationIntervals.get(i).get("object"));

			for (int typesIt = 0; typesIt < types.size(); typesIt++) {

				/** Nuevo tipo de clasificación para el interval **/

				IntervalAggregationDTO interval = new IntervalAggregationDTO();
				interval.setTimeInterval(timeInterval);

				String classificationName = types.get(typesIt).get("key").toString();

				ClassificationForPieChartDTO classification = getClassificationForName(classifications,
						classificationName);

				List<IntervalAggregationDTO> data = classification.getData();

				/** Obtiene todas las clasificaciones para recorrerlas por niveles **/
				List<Map<String, Object>> levels = ElasticSearchUtils.getBucketsFromAggregations(
						(Map<String, Object>) types.get(typesIt).get("objectClassification"));

				for (int levelIt = 0; levelIt < levels.size(); levelIt++) {

					/**
					 * Obtiene cada uno de los elementos de la clasificación
					 **/
					List<Map<String, Object>> objects = ElasticSearchUtils.getBucketsFromAggregations(
							(Map<String, Object>) levels.get(levelIt).get("objectClassificationPath"));

					for (int objectIt = 0; objectIt < objects.size(); objectIt++) { // objectos

						List<Map<String, Object>> type = ElasticSearchUtils.getBucketsFromAggregations(
								(Map<String, Object>) objects.get(objectIt).get("objectClassificationName"));
						/**
						 * Obtiene las estadisticas a nivel de tipo de classificación
						 **/
						Map<String, Object> stats = (Map<String, Object>) type.get(0).get("stats");

						interval.addCategory(objects.get(objectIt).get("key").toString(),
								type.get(0).get("key").toString(), getValue((Map<String, Object>) stats.get("value")));
					}
				}
				data.add(interval);
				classification.setData(data);

				addToClassifications(classifications, classification);
			}
		}
		classificationList.setClassification(classifications);
		return classificationList;
	}

	private ClassificationForPieChartDTO getClassificationForName(List<ClassificationForPieChartDTO> classifications,
			String classificationName) {

		for (int i = 0; i < classifications.size(); i++) {
			if (classifications.get(i).getName().equals(classificationName))
				return classifications.get(i);
		}

		ClassificationForPieChartDTO classification = new ClassificationForPieChartDTO();
		classification.setName(classificationName);
		classification.setData(new ArrayList<IntervalAggregationDTO>());
		return classification;
	}

	private void addToClassifications(List<ClassificationForPieChartDTO> classifications,
			ClassificationForPieChartDTO classification) {

		if (!classifications.contains(classification))
			classifications.add(classification);
	}
}
