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

import es.redmic.es.administrative.service.OrganisationESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.OrganisationRoleESService;
import es.redmic.models.es.administrative.dto.OrganisationCompactDTO;
import es.redmic.models.es.administrative.dto.OrganisationRoleDTO;
import es.redmic.models.es.administrative.model.OrganisationCompact;
import es.redmic.models.es.administrative.model.OrganisationRoles;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.RoleDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class OrganisationRoleESMapper extends CustomMapper<OrganisationRoles, OrganisationRoleDTO> {

	@Autowired
	private OrganisationESService organisationESService;

	@Autowired
	private OrganisationRoleESService organisationRoleESService;

	@Override
	public void mapAtoB(OrganisationRoles a, OrganisationRoleDTO b, MappingContext context) {

		b.setOrganisation(mapperFacade.map(a.getOrganisation(), OrganisationCompactDTO.class));
		b.setRole(mapperFacade.map(a.getRole(), RoleDTO.class));
	}

	@Override
	public void mapBtoA(OrganisationRoleDTO b, OrganisationRoles a, MappingContext context) {

		a.setOrganisation(mapperFacade.map(mapperFacade.newObject(b.getOrganisation(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(organisationESService)), OrganisationCompact.class));
		a.setRole((DomainES) mapperFacade.newObject(b.getRole(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(organisationRoleESService)));
	}
}
