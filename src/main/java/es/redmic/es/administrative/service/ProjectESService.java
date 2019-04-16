package es.redmic.es.administrative.service;

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
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.repository.ProjectESRepository;
import es.redmic.es.atlas.service.LayerESService;
import es.redmic.models.es.administrative.dto.ProjectDTO;
import es.redmic.models.es.administrative.model.Project;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Service
public class ProjectESService extends ActivityBaseAbstractESService<Project, ProjectDTO> {

	private String rankId = "2";

	@Autowired
	private LayerESService layerESService;

	ProjectESRepository repository;

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> projectGroupClassInReference = DomainES.class;
	/* Path de elastic para buscar por projectGroup */
	private String projectGroupPropertyPath = "projectGroup.id";

	@Autowired
	public ProjectESService(ProjectESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	@Override
	public Project mapper(ProjectDTO dtoToIndex) {
		Project model = super.mapper(dtoToIndex);

		model.setRank(getRank(getRankId()));
		return model;
	}

	/**
	 * Función para modificar las referencias de projectGroup en project en caso
	 * de ser necesario.
	 * 
	 * @param ReferencesES<DomainES>
	 *            clase que encapsula el modelo de projectGroup antes y después
	 *            de ser modificado.
	 */

	public void updateProjectGroup(ReferencesES<DomainES> reference) {

		updateReference(reference, projectGroupClassInReference, projectGroupPropertyPath);
	}

	@Override
	public void postUpdate(ReferencesES<Project> reference) {

		layerESService.updateActivity(reference);
	}

	public JSONCollectionDTO getProjectsByProgram(String programId) {

		DataSearchWrapper<Project> result = repository.findByParent(programId);
		return orikaMapper.getMapperFacade().map(result.getHits(), JSONCollectionDTO.class, getMappingContext());
	}

	public String getRankId() {
		return rankId;
	}

	public void setRankId(String rankId) {
		this.rankId = rankId;
	}
}
