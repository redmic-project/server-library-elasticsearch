package es.redmic.es.maintenance.domain.administrative.mapper;

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

import es.redmic.es.administrative.service.ContactESService;
import es.redmic.es.administrative.service.PlatformESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.ContactRoleESService;
import es.redmic.models.es.administrative.dto.ActivityPlatformRoleDTO;
import es.redmic.models.es.administrative.dto.ContactBaseDTO;
import es.redmic.models.es.administrative.dto.PlatformDTO;
import es.redmic.models.es.administrative.model.ContactCompact;
import es.redmic.models.es.administrative.model.PlatformCompact;
import es.redmic.models.es.administrative.model.PlatformContactRoles;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.RoleDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class PlatformContactRoleESMapper extends CustomMapper<PlatformContactRoles, ActivityPlatformRoleDTO> {

	@Autowired
	ContactESService contactESService;

	@Autowired
	private PlatformESService platformESService;

	@Autowired
	ContactRoleESService contactRoleESService;

	@Override
	public void mapAtoB(PlatformContactRoles a, ActivityPlatformRoleDTO b, MappingContext context) {

		b.setContact(mapperFacade.map(a.getContact(), ContactBaseDTO.class));
		b.setRole(mapperFacade.map(a.getRole(), RoleDTO.class));
		b.setPlatform(mapperFacade.map(a.getPlatform(), PlatformDTO.class));
	}

	@Override
	public void mapBtoA(ActivityPlatformRoleDTO b, PlatformContactRoles a, MappingContext context) {

		a.setContact(mapperFacade.map(mapperFacade.newObject(b.getContact(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(contactESService)), ContactCompact.class));
		a.setPlatform(mapperFacade.map(mapperFacade.newObject(b.getPlatform(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(platformESService)), PlatformCompact.class));
		a.setRole((DomainES) mapperFacade.newObject(b.getRole(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(contactRoleESService)));
	}
}
