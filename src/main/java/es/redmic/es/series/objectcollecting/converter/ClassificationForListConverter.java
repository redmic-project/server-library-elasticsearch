package es.redmic.es.series.objectcollecting.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.ElasticSearchUtils;
import es.redmic.models.es.geojson.common.model.Aggregations;
import es.redmic.models.es.series.objectcollecting.dto.ClassificationForListDTO;
import es.redmic.models.es.series.objectcollecting.dto.ClassificationsForListDTO;
import es.redmic.models.es.series.objectcollecting.dto.ObjectClassificationForListDTO;
import ma.glasnost.orika.metadata.Type;

@Component
public class ClassificationForListConverter
		extends ClassificationConverterBase<Aggregations, ClassificationsForListDTO> {

	@SuppressWarnings("unchecked")
	@Override
	public ClassificationsForListDTO convert(Aggregations source,
			Type<? extends ClassificationsForListDTO> destinationType) {

		ClassificationsForListDTO classificationList = new ClassificationsForListDTO();

		Map<String, Object> aggregations = source.getAttributes();
		if (aggregations == null || aggregations.size() == 0)
			return classificationList;

		List<Map<String, Object>> classificationType = ElasticSearchUtils.getBucketsFromAggregations(aggregations);

		List<ClassificationForListDTO> classifications = new ArrayList<ClassificationForListDTO>();
		for (int i = 0; i < classificationType.size(); i++) {
			/** Nuevo tipo de clasificación **/
			ClassificationForListDTO classification = new ClassificationForListDTO();
			classification.setName(classificationType.get(i).get("key").toString());
			/** Obtiene las estadisticas a nivel de tipo de classificación **/
			List<Map<String, Object>> timeIntervals = ElasticSearchUtils
					.getBucketsFromAggregations((Map<String, Object>) classificationType.get(i).get("timeIntervals"));
			for (int timeIntervalsIt = 0; timeIntervalsIt < timeIntervals.size(); timeIntervalsIt++) {
				classification.addHeader(timeIntervals.get(timeIntervalsIt).get("key_as_string").toString());
				classification.addV(getValue((Map<String, Object>) timeIntervals.get(timeIntervalsIt).get("value")));
			}
			/** Obtiene todas las clasificaciones del tipo actual **/
			List<Map<String, Object>> levels = ElasticSearchUtils.getBucketsFromAggregations(
					(Map<String, Object>) classificationType.get(i).get("objectClassification"));
			List<ObjectClassificationForListDTO> data = new ArrayList<ObjectClassificationForListDTO>();

			for (int levelIt = 0; levelIt < levels.size(); levelIt++) { // niveles de clasificación

				/** Obtiene cada uno de los elementos de la clasificación **/
				List<Map<String, Object>> objects = ElasticSearchUtils.getBucketsFromAggregations(
						(Map<String, Object>) levels.get(levelIt).get("objectClassificationPath"));
				boolean isLeave = (levelIt == levels.size() - 1);

				for (int objectIt = 0; objectIt < objects.size(); objectIt++) { // objectos
					ObjectClassificationForListDTO object = new ObjectClassificationForListDTO();
					object.initV(classification.getHeaderSize());
					object.setPath(objects.get(objectIt).get("key").toString());
					if (!isLeave)
						object.setLeaves(1);

					List<Map<String, Object>> type = ElasticSearchUtils.getBucketsFromAggregations(
							(Map<String, Object>) objects.get(objectIt).get("objectClassificationName"));
					object.setCategory(type.get(0).get("key").toString());

					List<Map<String, Object>> timeIntervalsObject = ElasticSearchUtils
							.getBucketsFromAggregations((Map<String, Object>) type.get(0).get("timeIntervals"));

					for (int timeIntervalsIt = 0; timeIntervalsIt < timeIntervalsObject.size(); timeIntervalsIt++) {
						// posición donde se debe insertar el dato y que depende del intervalo actual
						int pos = classification
								.getHeaderPos(timeIntervalsObject.get(timeIntervalsIt).get("key_as_string").toString());

						Map<String, Object> stats = (Map<String, Object>) timeIntervalsObject.get(timeIntervalsIt)
								.get("value");
						object.setV(pos, getValue(stats));
						// Setea el número de hijos si no es una hoja
						Integer count = (Integer) stats.get("count");
						if (!isLeave && (count > object.getLeaves()))
							object.setLeaves(count);
					}
					data.add(object);
				}
				classification.setData(data);
			}
			classifications.add(classification);
		}
		classificationList.setClassification(classifications);
		return classificationList;
	}
}
