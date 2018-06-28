package es.redmic.es.maintenance.parameter.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.parameter.service.ParameterTypeESService;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.parameter.dto.ParameterDTO;
import es.redmic.models.es.maintenance.parameter.dto.ParameterTypeDTO;
import es.redmic.models.es.maintenance.parameter.dto.ParameterUnitDTO;
import es.redmic.models.es.maintenance.parameter.model.Parameter;
import es.redmic.models.es.maintenance.parameter.model.ParameterUnit;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ParameterESMapper extends CustomMapper<Parameter, ParameterDTO> {

	@Autowired
	private ParameterTypeESService parameterTypeESService;

	@Override
	public void mapAtoB(Parameter a, ParameterDTO b, MappingContext context) {

		b.setParameterType(mapperFacade.map(a.getParameterType(), ParameterTypeDTO.class));
		b.setUnits(mapperFacade.mapAsList(a.getUnits(), ParameterUnitDTO.class));
	}

	@Override
	public void mapBtoA(ParameterDTO b, Parameter a, MappingContext context) {

		a.setParameterType((DomainES) mapperFacade.newObject(b.getParameterType(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(parameterTypeESService)));
		a.setUnits(mapperFacade.mapAsList(b.getUnits(), ParameterUnit.class));
	}
}
