package es.redmic.es.administrative.mapper;

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

import es.redmic.es.administrative.service.ProjectESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.ActivityRankESService;
import es.redmic.es.maintenance.domain.administrative.service.ActivityTypeESService;
import es.redmic.models.es.administrative.dto.ActivityBaseDTO;
import es.redmic.models.es.administrative.dto.ActivityDTO;
import es.redmic.models.es.administrative.model.Activity;
import es.redmic.models.es.administrative.model.Project;
import es.redmic.models.es.common.dto.DomainDTO;
import es.redmic.models.es.common.dto.DomainImplDTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.utils.HierarchicalUtils;
import es.redmic.models.es.maintenance.administrative.dto.ActivityTypeDTO;
import es.redmic.models.es.maintenance.administrative.model.ActivityType;
import ma.glasnost.orika.MappingContext;

@Component
public class ActivityESMapper extends ActivityBaseESMapper<Activity, ActivityDTO> {

	@Autowired
	private ProjectESService projectESService;

	@Autowired
	private ActivityTypeESService activityTypeESService;

	@Autowired
	ActivityRankESService rankESService;

	@Override
	public void mapAtoB(Activity a, ActivityDTO b, MappingContext context) {

		if (a.getActivityType() != null)
			b.setActivityType(mapperFacade.map(a.getActivityType(), ActivityTypeDTO.class));

		b.setParent(mapperFacade.map(getParent(a), ActivityBaseDTO.class));

		super.mapAtoB(a, b, context);
	}

	@Override
	public void mapBtoA(ActivityDTO b, Activity a, MappingContext context) {

		DomainDTO rankDTO = new DomainImplDTO();
		rankDTO.setId(3L);
		a.setRank((DomainES) mapperFacade.newObject(rankDTO, DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(rankESService)));

		a.setActivityType((ActivityType) mapperFacade.newObject(b.getActivityType(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(activityTypeESService)));

		a.setPath(getPath(b));
		super.mapBtoA(b, a, context);
	}

	private Project getParent(Activity activity) {

		String parentId = HierarchicalUtils.getParentId(activity.getPath());

		if (parentId == null)
			return null;

		return projectESService.findById(parentId);
	}

	public String getPath(ActivityDTO toIndex) {

		if (toIndex.getParent() == null)
			return "root" + "." + toIndex.getId();

		Project parent = projectESService.findById(Long.toString(toIndex.getParent().getId()));
		if (parent != null)
			return parent.getPath() + "." + toIndex.getId();

		return null;
	}
}
