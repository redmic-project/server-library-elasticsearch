package es.redmic.es.geodata.common.converter;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

@Component
public class CustomIdConverter extends CustomConverter<String, Long> {

	@Override
	public Long convert(String source, Type<? extends Long> destinationType) {
		String[] idSplit = source.split("-");
		if (idSplit.length > 0)
			return Long.parseLong(idSplit[1]);
		return Long.parseLong(source);
	}
}
