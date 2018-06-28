package es.redmic.es.administrative.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.ProgramESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.ActivityRankESService;
import es.redmic.es.maintenance.domain.administrative.service.ProjectGroupESService;
import es.redmic.models.es.administrative.dto.ActivityBaseDTO;
import es.redmic.models.es.administrative.dto.ProjectDTO;
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
		b.setParent(mapperFacade.map(getParent(a), ActivityBaseDTO.class));
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
		super.mapBtoA(b, a, context);
	}

	private Program getParent(Project project) {

		String parentId = HierarchicalUtils.getParentId(project.getPath());

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
