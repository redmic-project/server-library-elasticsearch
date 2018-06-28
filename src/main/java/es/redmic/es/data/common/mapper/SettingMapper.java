package es.redmic.es.data.common.mapper;

import org.springframework.stereotype.Component;

import es.redmic.models.es.common.dto.SettingsDTO;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class SettingMapper extends CustomMapper<DataHitWrapper, SettingsDTO> {

	@Override
	public void mapAtoB(DataHitWrapper a, SettingsDTO b, MappingContext context) {
		
		mapperFacade.map(a.get_source(), b);
    }
}