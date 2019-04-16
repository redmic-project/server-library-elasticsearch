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

import es.redmic.es.administrative.repository.ContactESRepository;
import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.geodata.geofixedstation.service.GeoFixedTimeSeriesESService;
import es.redmic.es.maintenance.device.service.DeviceESService;
import es.redmic.models.es.administrative.dto.ContactDTO;
import es.redmic.models.es.administrative.model.Contact;
import es.redmic.models.es.administrative.model.Organisation;
import es.redmic.models.es.administrative.model.OrganisationCompact;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

@Service
public class ContactESService extends MetaDataESService<Contact, ContactDTO> {

	@Autowired
	ActivityESService activityESService;
	
	@Autowired
	ProjectESService projectESService;
	
	@Autowired
	ProgramESService programESService;
	
	@Autowired
	PlatformESService platformESService;
	
	@Autowired
	DeviceESService deviceESService;
	
	@Autowired
	GeoFixedTimeSeriesESService geoFixedTimeSeriesESService;
	
	private static Class<OrganisationCompact> organisationClassInReference = OrganisationCompact.class;
	/*Path de elastic para buscar por organisation*/
	private static String organisationPropertyPath = "affiliation.id";

	@Autowired
	public ContactESService(ContactESRepository repository) {
		super(repository);
	}
	
	public JSONCollectionDTO getActivities(DataQueryDTO dto, Long id) {
		
		return activityESService.getActivitiesByContact(dto, id);
	}
	
	/**
	 * Función para modificar las referencias de organisation en contact en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de organisation antes y después
	 *            de ser modificado.
	 */

	public void updateOrganisation(ReferencesES<Organisation> reference) {

		updateReference(reference, organisationClassInReference, organisationPropertyPath);
	}
	
	@Override
	public void postUpdate(ReferencesES<Contact> reference) {
		
		platformESService.updateContact(reference);
		activityESService.updateContact(reference);
		activityESService.updateContactsPlatform(reference);
		projectESService.updateContact(reference);
		projectESService.updateContactsPlatform(reference);
		programESService.updateContact(reference);
		programESService.updateContactsPlatform(reference);
		deviceESService.updateContact(reference);
		geoFixedTimeSeriesESService.updateContact(reference);
	}
}
