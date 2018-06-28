package es.redmic.es.common.utils;

import java.util.HashMap;
import java.util.Map;

import es.redmic.es.data.common.service.RWDataESService;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.model.BaseES;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

public abstract class DataMapperUtils {

	public static Long convertIdentifier(String id, String geoDataPrefixType) {
		String idResult = id.replace(geoDataPrefixType + "-", "");
		if (idResult.contains("-"))
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		return Long.parseLong(idResult);
	}

	public static String convertIdentifier(Long id, String geoDataPrefixType) {
		return geoDataPrefixType + "-" + id;
	}

	public static Type<BaseES<?>> getBaseType() {
		return TypeFactory.<BaseES<?>>valueOf(BaseES.class);
	}

	public static MappingContext getObjectFactoryContext(RWDataESService<?, ?> service) {

		Map<Object, Object> properties = new HashMap<Object, Object>();
		properties.put("service", service);
		return new MappingContext(properties);
	}
}
