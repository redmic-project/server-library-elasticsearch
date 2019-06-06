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

import es.redmic.es.administrative.repository.ActivityESRepository;
import es.redmic.models.es.administrative.dto.ActivityDTO;
import es.redmic.models.es.administrative.model.Activity;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.models.es.maintenance.administrative.model.ActivityType;
import es.redmic.models.es.maintenance.administrative.model.ActivityTypeCompact;

@Service
public class ActivityESService extends ActivityBaseAbstractESService<Activity, ActivityDTO> {

	private String rankId = "3";

	private ActivityESRepository repository;

	/* Clase del modelo indexado en la referencia */
	private static Class<ActivityTypeCompact> activityTypeClassInReference = ActivityTypeCompact.class;
	/* Path de elastic para buscar por accessibility */
	private String activityTypePropertyPath = "activityType.id";

	@Autowired
	public ActivityESService(ActivityESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	@Override
	public Activity mapper(ActivityDTO dtoToIndex) {
		Activity model = super.mapper(dtoToIndex);

		model.setRank(getRank(getRankId()));
		return model;
	}

	/**
	 * Función para modificar las referencias de activityType en activity en caso de
	 * ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de activityType antes y después de
	 *            ser modificado.
	 */

	public void updateActivityType(ReferencesES<ActivityType> reference) {

		updateReference(reference, activityTypeClassInReference, activityTypePropertyPath);
	}

	/**
	 * Función para modificar las referencias de activityBase en su repositorio en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de activityBase antes y después de
	 *            ser modificado.
	 */

	public void updateActivityBase(ReferencesES<Activity> reference) {
	}

	@Override
	public void postUpdate(ReferencesES<Activity> reference) {
		updateActivityBase(reference);
	}

	public JSONCollectionDTO findBySpecies(DataQueryDTO dto, String speciesId) {

		DataSearchWrapper<Activity> result = repository.findBySpecies(dto, speciesId);
		return orikaMapper.getMapperFacade().map(result.getHits(), JSONCollectionDTO.class, getMappingContext());
	}

	public JSONCollectionDTO getActivitiesByProject(String projectId) {

		DataSearchWrapper<Activity> result = repository.findByParent(projectId);
		return orikaMapper.getMapperFacade().map(result.getHits(), JSONCollectionDTO.class, getMappingContext());
	}

	public JSONCollectionDTO getActivitiesByContact(DataQueryDTO dto, Long contactId) {

		DataSearchWrapper<Activity> result = repository.findByContacts(dto, contactId);
		return orikaMapper.getMapperFacade().map(result.getHits(), JSONCollectionDTO.class, getMappingContext());
	}

	public JSONCollectionDTO getActivitiesByOrganisation(DataQueryDTO dto, Long organisationId) {

		DataSearchWrapper<Activity> result = repository.findByOrganisations(dto, organisationId);
		return orikaMapper.getMapperFacade().map(result.getHits(), JSONCollectionDTO.class, getMappingContext());
	}

	public JSONCollectionDTO getActivitiesByDocument(DataQueryDTO dto, Long documentId) {

		DataSearchWrapper<Activity> result = repository.findByDocuments(dto, documentId);
		return orikaMapper.getMapperFacade().map(result.getHits(), JSONCollectionDTO.class, getMappingContext());
	}

	public JSONCollectionDTO getActivitiesByPlatform(DataQueryDTO dto, Long platformId) {

		DataSearchWrapper<Activity> result = repository.findByPlatforms(dto, platformId);
		return orikaMapper.getMapperFacade().map(result.getHits(), JSONCollectionDTO.class, getMappingContext());
	}

	@Override
	public String getRankId() {
		return rankId;
	}

	@Override
	public void setRankId(String rankId) {
		this.rankId = rankId;
	}
}
