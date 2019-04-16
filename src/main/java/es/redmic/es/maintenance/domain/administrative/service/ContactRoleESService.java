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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.service.ActivityESService;
import es.redmic.es.administrative.service.PlatformESService;
import es.redmic.es.administrative.service.ProgramESService;
import es.redmic.es.administrative.service.ProjectESService;
import es.redmic.es.common.service.DomainESService;
import es.redmic.es.geodata.geofixedstation.service.GeoFixedTimeSeriesESService;
import es.redmic.es.maintenance.domain.administrative.repository.ContactRoleESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.administrative.dto.ContactRoleDTO;

@Service
public class ContactRoleESService extends DomainESService<DomainES, ContactRoleDTO> {

	@Autowired
	ActivityESService activityESService;
	
	@Autowired
	ProjectESService projectESService;
	
	@Autowired
	ProgramESService programESService;
	
	@Autowired
	PlatformESService platformESService;
	
	@Autowired
	GeoFixedTimeSeriesESService geoFixedTimeSeriesESService;
	
	@Autowired
	public ContactRoleESService(ContactRoleESRepository repository) {
		super(repository);
	}

	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		
		activityESService.updatePlatformContactRoles(reference);
		activityESService.updateContactOrganisationRoles(reference);
		projectESService.updatePlatformContactRoles(reference);
		projectESService.updateContactOrganisationRoles(reference);
		programESService.updatePlatformContactRoles(reference);
		programESService.updateContactOrganisationRoles(reference);
		platformESService.updateOrganisationContactRoles(reference);
		geoFixedTimeSeriesESService.updateContactRole(reference);
	}

	@Override
	protected void postSave(Object model) {}

	@Override
	protected void preDelete(Object object) {}

	@Override
	protected void postDelete(String id) {};
}
