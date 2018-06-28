package es.redmic.es.common.objectFactory;

import org.springframework.stereotype.Component;

import es.redmic.es.data.common.service.RWDataESService;
import es.redmic.models.es.common.dto.DTO;
import es.redmic.models.es.common.model.BaseES;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.ObjectFactory;

@Component
public class ModelESFactory implements ObjectFactory<BaseES<?>> {

	@Override
	public BaseES<?> create(Object source, MappingContext mappingContext) {
		
		if (source == null || !(source instanceof DTO))
			return null;
		
		RWDataESService<?, ?> service = (RWDataESService<?, ?>) mappingContext.getProperty("service");
		
		DTO dto = (DTO) source;
		if (dto != null && dto.getId() != null)
			return (BaseES<?>) service.findById(dto.getId().toString());
		return null;
	}
}