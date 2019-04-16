package es.redmic.es.series.attributeseries.mapper;

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
import es.redmic.es.maintenance.qualifiers.service.AttributeTypeESService;
import es.redmic.models.es.maintenance.qualifiers.dto.AttributeTypeBaseDTO;
import es.redmic.models.es.maintenance.qualifiers.model.AttributeTypeBase;
import es.redmic.models.es.series.attributeseries.dto.AttributeSeriesDTO;
import es.redmic.models.es.series.attributeseries.model.AttributeSeries;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class AttributeSeriesESMapper extends CustomMapper<AttributeSeries, AttributeSeriesDTO> {
	
	@Autowired
	AttributeTypeESService attributeTypeESService;
	
	@Override
	public void mapAtoB(AttributeSeries a, AttributeSeriesDTO b, MappingContext context) {
		
		if (a.getAttributeType() != null)
			b.setAttributeType(mapperFacade.map(a.getAttributeType(), AttributeTypeBaseDTO.class));
	}
	
	@Override
	public void mapBtoA(AttributeSeriesDTO b, AttributeSeries a, MappingContext context) {
		
		if (b.getAttributeType() != null) {
			a.setAttributeType(mapperFacade.map(mapperFacade.newObject(b.getAttributeType(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(attributeTypeESService)), AttributeTypeBase.class));
		}
	}
}
