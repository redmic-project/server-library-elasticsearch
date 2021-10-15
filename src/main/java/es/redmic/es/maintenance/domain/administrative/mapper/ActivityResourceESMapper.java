package es.redmic.es.maintenance.domain.administrative.mapper;

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
import es.redmic.es.maintenance.domain.administrative.service.ResourceTypeESService;
import es.redmic.models.es.administrative.dto.ActivityResourceDTO;
import es.redmic.models.es.administrative.model.ActivityResource;
import es.redmic.models.es.maintenance.administrative.dto.ResourceTypeDTO;
import es.redmic.models.es.maintenance.administrative.model.ResourceType;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ActivityResourceESMapper extends CustomMapper<ActivityResource, ActivityResourceDTO> {

	@Autowired
	ResourceTypeESService resourceTypeESService;

	@Override
	public void mapAtoB(ActivityResource a, ActivityResourceDTO b, MappingContext context) {

		b.setResourceType(mapperFacade.map(a.getResourceType(), ResourceTypeDTO.class));
	}

	@Override
	public void mapBtoA(ActivityResourceDTO b, ActivityResource a, MappingContext context) {

		a.setResourceType(mapperFacade.map(mapperFacade.newObject(b.getResourceType(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(resourceTypeESService)), ResourceType.class));
	}
}
