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
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.ActivityRankESService;
import es.redmic.es.maintenance.domain.administrative.service.ProjectGroupESService;
import es.redmic.models.es.administrative.dto.ActivityBaseDTO;
import es.redmic.models.es.administrative.dto.ProjectDTO;
import es.redmic.models.es.administrative.model.ActivityCompact;
import es.redmic.models.es.administrative.model.Program;
import es.redmic.models.es.administrative.model.Project;
import es.redmic.models.es.common.dto.DomainDTO;
import es.redmic.models.es.common.dto.DomainImplDTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.utils.HierarchicalUtils;
import es.redmic.models.es.maintenance.administrative.dto.ProjectGroupDTO;
import ma.glasnost.orika.MappingContext;

@Component
public class ProjectESMapper extends ActivityBaseESMapper<Project, ProjectDTO> {

	@Autowired
	private ProgramESService programESService;

	@Autowired
	private ProjectGroupESService projectGroupESService;

	@Autowired
	ActivityRankESService rankESService;

	@Override
	public void mapAtoB(Project a, ProjectDTO b, MappingContext context) {

		b.setProjectGroup(mapperFacade.map(a.getProjectGroup(), ProjectGroupDTO.class));
		b.setParent(mapperFacade.map(getParent(a.getPath()), ActivityBaseDTO.class));
		super.mapAtoB(a, b, context);
	}

	@Override
	public void mapBtoA(ProjectDTO b, Project a, MappingContext context) {

		DomainDTO rankDTO = new DomainImplDTO();
		rankDTO.setId(2L);
		a.setRank((DomainES) mapperFacade.newObject(rankDTO, DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(rankESService)));

		a.setProjectGroup((DomainES) mapperFacade.newObject(b.getProjectGroup(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(projectGroupESService)));
		a.setPath(getPath(b));
		a.setParent(mapperFacade.map(getParent(b.getPath()), ActivityCompact.class));
		super.mapBtoA(b, a, context);
	}

	private Program getParent(String path) {

		String parentId = HierarchicalUtils.getParentId(path);

		if (parentId == null)
			return null;

		return programESService.findById(parentId);
	}

	public String getPath(ProjectDTO toIndex) {

		if (toIndex.getParent() == null)
			return "root" + "." + toIndex.getId();

		Program parent = programESService.findById(Long.toString(toIndex.getParent().getId()));
		if (parent != null)
			return parent.getPath() + "." + toIndex.getId();

		return null;
	}
}
