package es.redmic.es.series.timeseries.converter;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import es.redmic.models.es.series.timeseries.dto.DataHistogramStatsDTO;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

@SuppressWarnings("rawtypes")
@Component
public class TimeSeriesRawConverter extends CustomConverter<LinkedHashMap, DataHistogramStatsDTO> {

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
