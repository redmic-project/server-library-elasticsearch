package es.redmic.es.maintenance.domain.administrative.service;

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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.service.ActivityESService;
import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.maintenance.domain.administrative.repository.ActivityTypeESRepository;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.administrative.dto.ActivityTypeDTO;
import es.redmic.models.es.maintenance.administrative.model.ActivityType;

@Service
public class ActivityTypeESService extends MetaDataESService<ActivityType, ActivityTypeDTO> {

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> activityFieldClassInReference = DomainES.class;
	/* Path de elastic para buscar por accessibility */
	private String activityFieldPropertyPath = "activityField.id";

	@Autowired
	ActivityFieldESService activityFieldESService;

	@Autowired
	ActivityESService activityESService;

	@Autowired
	public ActivityTypeESService(ActivityTypeESRepository repository) {
		super(repository);
	}

	@Override
	public void postUpdate(ReferencesES<ActivityType> reference) {

		activityESService.updateActivityType(reference);
	}

	/**
	 * Función para modificar las referencias de activityField en activityType
	 * en caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de activityField antes y después
	 *            de ser modificado.
	 */

	public void updateActivityField(ReferencesES<DomainES> reference) {

		updateReference(reference, activityFieldClassInReference, activityFieldPropertyPath);
	}

	public List<String> getActivityCategoriesByActivityType(Long activityTypeId) {

		return DataPrefixType.getActivityCategoriesByActivityType(activityTypeId);
	}
}
