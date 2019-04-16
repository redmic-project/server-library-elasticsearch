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
import es.redmic.es.maintenance.domain.administrative.service.ActivityFieldESService;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.ActivityFieldDTO;
import es.redmic.models.es.maintenance.administrative.dto.ActivityTypeDTO;
import es.redmic.models.es.maintenance.administrative.model.ActivityType;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ActivityTypeESMapper extends CustomMapper<ActivityType, ActivityTypeDTO> {

	@Autowired
	ActivityFieldESService activityFieldService;

	@Override
	public void mapAtoB(ActivityType a, ActivityTypeDTO b, MappingContext context) {

		b.setActivityField(mapperFacade.map(a.getActivityField(), ActivityFieldDTO.class));
	}

	@Override
	public void mapBtoA(ActivityTypeDTO b, ActivityType a, MappingContext context) {

		a.setActivityField((DomainES) mapperFacade.newObject(b.getActivityField(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(activityFieldService)));
	}
}
