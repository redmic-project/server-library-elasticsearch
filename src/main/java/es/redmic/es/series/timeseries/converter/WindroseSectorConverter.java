package es.redmic.es.series.timeseries.converter;

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
