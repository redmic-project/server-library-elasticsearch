package es.redmic.es.series.timeseries.converter;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.ElasticSearchUtils;
import es.redmic.models.es.geojson.common.model.Aggregations;
import es.redmic.models.es.series.timeseries.dto.DatesByDirectionDTO;
import es.redmic.models.es.series.timeseries.dto.DatesByDirectionListDTO;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

@Component
public class DirectionDatesConverter extends CustomConverter<Aggregations, DatesByDirectionListDTO> {

	@Override
	public DatesByDirectionListDTO convert(Aggregations source,
			Type<? extends DatesByDirectionListDTO> destinationType) {

		DatesByDirectionListDTO result = new DatesByDirectionListDTO();

		Map<String, Object> aggregations = source.getAttributes();
		if (aggregations == null || aggregations.size() == 0)
			return result;

		List<Map<String, Object>> sector = ElasticSearchUtils.getBucketsFromAggregations(aggregations);

		for (int i = 0; i < sector.size(); i++) {
			// por cada uno de los sectores
			DatesByDirectionDTO datesDTO = new DatesByDirectionDTO();
			List<Map<String, Object>> dates = ElasticSearchUtils.getBucketsFromAggregations(sector.get(i));

			for (int j = 0; j < dates.size(); j++) {
				datesDTO.getDates().add(dates.get(j).get("key_as_string").toString());
			}
			result.add(datesDTO);
		}
		return result;
	}
}
