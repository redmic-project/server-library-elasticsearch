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

import es.redmic.es.administrative.repository.OrganisationESRepository;
import es.redmic.es.common.service.MetaDataESService;
import es.redmic.models.es.administrative.dto.OrganisationDTO;
import es.redmic.models.es.administrative.model.Organisation;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.maintenance.administrative.model.Country;

@Service
public class OrganisationESService extends MetaDataESService<Organisation, OrganisationDTO> {
	
	@Autowired
	ActivityESService activityESService;
	
	@Autowired
	ProjectESService projectESService;
	
	@Autowired
	ProgramESService programESService;

	@Autowired
	PlatformESService platformESService;
	
	@Autowired
	ContactESService contactESService;
	
	/*Clase del modelo indexado en la referencia*/
	private static Class<Country> countryClassInReference = Country.class;
	/*Path de elastic para buscar por country*/
	private static String countryPropertyPath = "country.id";
	
	/*Clase del modelo indexado en la referencia*/
	private static Class<DomainES> organisationTypeClassInReference = DomainES.class;
	/*Path de elastic para buscar por organisationType*/
	private static String organisationTypePropertyPath = "organisationType.id";

	@Autowired
	public OrganisationESService(OrganisationESRepository repository) {
		super(repository);
	}

	public JSONCollectionDTO getActivities(DataQueryDTO dto, Long id) {
		
		return activityESService.getActivitiesByOrganisation(dto, id);
	}
	
	/**
	 * Función para modificar las referencias de country en organisation en caso de ser necesario.
	 * 
	 * @param ReferencesES<DomainES> clase que encapsula el modelo de country antes y después de ser modificado.
	 */

	public void updateCountry(ReferencesES<Country> reference) {
		
		updateReference(reference, countryClassInReference, countryPropertyPath);
	}
	
	/**
	 * Función para modificar las referencias de organisationType en organisation en caso de ser necesario.
	 * 
	 * @param ReferencesES<DomainES> clase que encapsula el modelo de organisationType antes y después de ser modificado.
	 */

	public void updateOrganisationType(ReferencesES<DomainES> reference) {
		
		updateReference(reference, organisationTypeClassInReference, organisationTypePropertyPath);
	}
	
	
	@Override
	public void postUpdate(ReferencesES<Organisation> reference) {
		
		platformESService.updateOrganisation(reference);
		activityESService.updateOrganisation(reference);
		activityESService.updateOrganisationsContact(reference);
		projectESService.updateOrganisation(reference);
		projectESService.updateOrganisationsContact(reference);
		programESService.updateOrganisation(reference);
		programESService.updateOrganisationsContact(reference);
		contactESService.updateOrganisation(reference);
	}
}
