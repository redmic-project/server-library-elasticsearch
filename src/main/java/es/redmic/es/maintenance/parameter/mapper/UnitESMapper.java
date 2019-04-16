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
import es.redmic.es.maintenance.parameter.service.UnitTypeESService;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.parameter.dto.UnitDTO;
import es.redmic.models.es.maintenance.parameter.dto.UnitTypeDTO;
import es.redmic.models.es.maintenance.parameter.model.Unit;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class UnitESMapper extends CustomMapper<Unit, UnitDTO> {

	@Autowired
	private UnitTypeESService unitTypeESService;

	@Override
	public void mapAtoB(Unit a, UnitDTO b, MappingContext context) {

		b.setUnitType(mapperFacade.map(a.getUnitType(), UnitTypeDTO.class));
	}

	@Override
	public void mapBtoA(UnitDTO b, Unit a, MappingContext context) {

		if (b.getUnitType() != null)
			a.setUnitType((DomainES) mapperFacade.newObject(b.getUnitType(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(unitTypeESService)));
	}
}
