package es.redmic.es.series.common.converter;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import es.redmic.models.es.series.timeseries.dto.DataHistogramItemDTO;
import es.redmic.models.es.series.timeseries.dto.DataHistogramStatsDTO;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

@SuppressWarnings("rawtypes")
@Component
public class ItemHistogramConverter extends CustomConverter<LinkedHashMap, DataHistogramItemDTO> {

	@Override
	public DataHistogramItemDTO convert(LinkedHashMap source, Type<? extends DataHistogramItemDTO> destinationType) {
		DataHistogramItemDTO item = new DataHistogramItemDTO();
		item.setKey_as_string((String) source.get("key_as_string"));
		item.setValue(mapperFacade.convert(source.get("value"), DataHistogramStatsDTO.class, null));
		return item;
	}
}
