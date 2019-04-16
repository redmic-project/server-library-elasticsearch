package es.redmic.es.series.objectcollecting.mapper;

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
import es.redmic.es.maintenance.objects.service.ObjectTypeESService;
import es.redmic.models.es.maintenance.objects.dto.ObjectCollectingDTO;
import es.redmic.models.es.maintenance.objects.dto.ObjectTypeBaseDTO;
import es.redmic.models.es.maintenance.objects.model.ObjectCollecting;
import es.redmic.models.es.maintenance.objects.model.ObjectTypeBase;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ObjectCollectingESMapper extends CustomMapper<ObjectCollecting, ObjectCollectingDTO> {
	
	@Autowired
	ObjectTypeESService objectService;
	
	@Override
	public void mapAtoB(ObjectCollecting a, ObjectCollectingDTO b, MappingContext context) {
		
		b.setObjectType(mapperFacade.map(a.getObjectType(), ObjectTypeBaseDTO.class));
	}
	
	@Override
	public void mapBtoA(ObjectCollectingDTO b, ObjectCollecting a, MappingContext context) {
				
		a.setObjectType(mapperFacade.map(mapperFacade.newObject(b.getObjectType(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(objectService)), ObjectTypeBase.class));
				
	}
}
