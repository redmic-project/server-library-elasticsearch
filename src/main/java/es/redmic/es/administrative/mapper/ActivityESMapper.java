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

import es.redmic.es.administrative.service.ProgramESService;
import es.redmic.es.administrative.service.ProjectESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.ActivityRankESService;
import es.redmic.es.maintenance.domain.administrative.service.ActivityTypeESService;
import es.redmic.es.maintenance.domain.administrative.service.ThemeInspireESService;
import es.redmic.models.es.administrative.dto.ActivityBaseDTO;
import es.redmic.models.es.administrative.dto.ActivityDTO;
import es.redmic.models.es.administrative.dto.ActivityResourceDTO;
import es.redmic.models.es.administrative.model.Activity;
import es.redmic.models.es.administrative.model.ActivityCompact;
import es.redmic.models.es.administrative.model.Project;
import es.redmic.models.es.administrative.model.ActivityResource;
import es.redmic.models.es.administrative.model.Program;
import es.redmic.models.es.common.dto.DomainDTO;
import es.redmic.models.es.common.dto.DomainImplDTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.utils.HierarchicalUtils;
import es.redmic.models.es.maintenance.administrative.dto.ActivityTypeDTO;
import es.redmic.models.es.maintenance.administrative.dto.ThemeInspireDTO;
import es.redmic.models.es.maintenance.administrative.model.ActivityType;
import es.redmic.models.es.maintenance.administrative.model.ThemeInspire;
import ma.glasnost.orika.MappingContext;

@Component
public class ActivityESMapper extends ActivityBaseESMapper<Activity, ActivityDTO> {

	@Autowired
	private ProjectESService projectESService;

	@Autowired
	private ProgramESService programESService;

	@Autowired
	private ActivityTypeESService activityTypeESService;

	@Autowired
	ActivityRankESService rankESService;

	@Autowired
	ThemeInspireESService themeInspireESService;

	@Override
	public void mapAtoB(Activity a, ActivityDTO b, MappingContext context) {

		if (a.getActivityType() != null)
			b.setActivityType(mapperFacade.map(a.getActivityType(), ActivityTypeDTO.class));

		b.setParent(mapperFacade.map(getParent(a.getPath()), ActivityBaseDTO.class));

		b.setGrandparent(mapperFacade.map(getGrandparent(a.getPath()), ActivityBaseDTO.class));

		if (a.getThemeInspire() != null) {
			b.setThemeInspire(mapperFacade.map(a.getThemeInspire(), ThemeInspireDTO.class));
		}

		if (a.getResources() != null)
			b.setResources(mapperFacade.mapAsList(a.getResources(), ActivityResourceDTO.class));

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

		a.setParent(mapperFacade.map(getParent(b.getPath()), ActivityCompact.class));
		a.setGrandparent(mapperFacade.map(getGrandparent(a.getPath()), ActivityCompact.class));

		if (b.getThemeInspire() != null) {
			a.setThemeInspire((ThemeInspire) mapperFacade.newObject(b.getThemeInspire(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(themeInspireESService)));
		}

		a.setResources(mapperFacade.mapAsList(b.getResources(), ActivityResource.class));

		super.mapBtoA(b, a, context);
	}

	private Project getParent(String path) {

		String parentId = HierarchicalUtils.getParentId(path);

		if (parentId == null)
			return null;

		return projectESService.findById(parentId);
	}

	private Program getGrandparent(String path) {

		String grandParentId = HierarchicalUtils.getGrandparentId(path);

		if (grandParentId == null)
			return null;

		return programESService.findById(grandParentId);
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
