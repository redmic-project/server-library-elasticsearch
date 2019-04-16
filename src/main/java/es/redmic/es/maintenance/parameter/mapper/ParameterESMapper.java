package es.redmic.es.maintenance.parameter.mapper;

/*-
 * #%L
 * ElasticSearch
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
