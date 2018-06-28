package es.redmic.es.series.common.converter;

import org.springframework.stereotype.Component;

import es.redmic.models.es.maintenance.parameter.dto.DataDefinitionDTO;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

@Component
public class DataDefinitionConverter extends BidirectionalConverter<Long, DataDefinitionDTO> {

	@Override
	public DataDefinitionDTO convertTo(Long source, Type<DataDefinitionDTO> destinationType) {
		DataDefinitionDTO dto = new DataDefinitionDTO();
		dto.setId(source);
		return dto;
	}

	@Override
	public Long convertFrom(DataDefinitionDTO source, Type<Long> destinationType) {
		return source.getId();
	}
}
