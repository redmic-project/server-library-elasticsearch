package es.redmic.es.series.common.converter;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import es.redmic.es.series.objectcollecting.converter.ClassificationConverterBase;
import es.redmic.models.es.geojson.common.model.Aggregations;
import es.redmic.models.es.series.timeseries.dto.DataHistogramDTO;
import es.redmic.models.es.series.timeseries.dto.DataHistogramItemDTO;
import ma.glasnost.orika.metadata.Type;

@Component
public class DataHistogramConverter extends ClassificationConverterBase<Aggregations, DataHistogramDTO> {

	@SuppressWarnings("unchecked")
	@Override
	public DataHistogramDTO convert(Aggregations source, Type<? extends DataHistogramDTO> destinationType) {

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
			DataHistogramItemDTO item = mapperFacade.convert(hits.get(i), DataHistogramItemDTO.class, null);
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
