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

import es.redmic.es.administrative.repository.PlatformESRepository;
import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.maintenance.device.service.DeviceESService;
import es.redmic.models.es.administrative.dto.PlatformDTO;
import es.redmic.models.es.administrative.model.Contact;
import es.redmic.models.es.administrative.model.ContactCompact;
import es.redmic.models.es.administrative.model.Organisation;
import es.redmic.models.es.administrative.model.OrganisationCompact;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

@Service
public class PlatformESService extends MetaDataESService<Platform, PlatformDTO> {

	@Autowired
	ActivityESService activityESService;

	@Autowired
	ProjectESService projectESService;

	@Autowired
	ProgramESService programESService;

	@Autowired
	DeviceESService deviceESService;

	private static Class<DomainES> domainClassInReference = DomainES.class;
	/* Path de elastic para buscar por platformType */
	private String platformTypePropertyPath = "platformType.id";

	/* Path de elastic para buscar por organisation */
	private String organisationPropertyPath = "organisation.id";

	/* Path de elastic para buscar por contact */
	private String contactPropertyPath = "contacts.contact.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<ContactCompact> contactClassInReference = ContactCompact.class;

	/* Path de elastic para buscar por organisation dentro de contact */
	private String contactOrganisationPropertyPath = "contacts.organisation.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<OrganisationCompact> organisationClassInReference = OrganisationCompact.class;
	
	private String contactOrganisationRolesPropertyPath = "contacts.role.id";
	
	PlatformESRepository repository;
	
	int nestingDepthRelations = 2;

	@Autowired
	public PlatformESService(PlatformESRepository repository) {
		super(repository);
		this.repository = repository;
	}
	
	public PlatformDTO findByUuid(String uuid) {
		return repository.findByUuid(uuid);
	}

	public JSONCollectionDTO getActivities(DataQueryDTO dto, Long id) {
		
		return activityESService.getActivitiesByPlatform(dto, id);
	}

	/**
	 * Función para modificar las referencias de platformType en platform en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de platformType antes y después
	 *            de ser modificado.
	 */

	public void updatePlatformType(ReferencesES<DomainES> reference) {

		updateReference(reference, domainClassInReference, platformTypePropertyPath);
	}

	/**
	 * Función para modificar las referencias de organisation en platform en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de organisation antes y después
	 *            de ser modificado.
	 */

	public void updateOrganisation(ReferencesES<Organisation> reference) {

		updateReference(reference, organisationClassInReference, organisationPropertyPath);
	}

	/**
	 * Función para modificar las referencias de contact en platform en caso de
	 * ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de contact antes y después de
	 *            ser modificado.
	 */

	public void updateContact(ReferencesES<Contact> reference) {

		updateReferenceByScript(reference, contactClassInReference, contactPropertyPath, nestingDepthRelations);
	}

	/**
	 * Función para modificar las referencias de organisations en contactos de platform en caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de contactRoles antes y después
	 *            de ser modificado.
	 */

	public void updateContactOrganisation(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, organisationClassInReference, contactOrganisationPropertyPath, nestingDepthRelations);
	}
	
	/**
	 * Función para modificar las referencias de roles en contact dentro de
	 * organisaciones de platform en caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de organisationRoles antes y
	 *            después de ser modificado.
	 */

	public void updateOrganisationContactRoles(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, domainClassInReference, contactOrganisationRolesPropertyPath, nestingDepthRelations);
	}

	@Override
	public void postUpdate(ReferencesES<Platform> reference) {

		deviceESService.updatePlatform(reference);
		activityESService.updatePlatform(reference);
		programESService.updatePlatform(reference);
		projectESService.updatePlatform(reference);
	}
}
