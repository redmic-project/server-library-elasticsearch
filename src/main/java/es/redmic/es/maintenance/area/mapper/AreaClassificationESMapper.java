package es.redmic.es.maintenance.area.mapper;

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
import es.redmic.es.maintenance.area.service.ThematicTypeESService;
import es.redmic.models.es.maintenance.areas.dto.AreaClassificationDTO;
import es.redmic.models.es.maintenance.areas.dto.ThematicTypeBaseDTO;
import es.redmic.models.es.maintenance.areas.model.AreaClassification;
import es.redmic.models.es.maintenance.common.model.ClassificationBase;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class AreaClassificationESMapper extends CustomMapper<AreaClassification, AreaClassificationDTO> {

	@Autowired
	ThematicTypeESService thematicTypeESService;

	@Override
	public void mapAtoB(AreaClassification a, AreaClassificationDTO b, MappingContext context) {

		if (a.getType() != null) {
			b.setType(mapperFacade.map(a.getType(), ThematicTypeBaseDTO.class));
		}
	}

	@Override
	public void mapBtoA(AreaClassificationDTO b, AreaClassification a, MappingContext context) {

		if (b.getType() != null) {
			a.setType(mapperFacade.map(mapperFacade.newObject(b.getType(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(thematicTypeESService)), ClassificationBase.class));
		}
	}
}
