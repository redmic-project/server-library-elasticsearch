package es.redmic.es.administrative.mapper;

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
import es.redmic.es.maintenance.domain.administrative.service.PlatformTypeESService;
import es.redmic.models.es.administrative.dto.ContactOrganisationRoleDTO;
import es.redmic.models.es.administrative.dto.OrganisationDTO;
import es.redmic.models.es.administrative.dto.PlatformDTO;
import es.redmic.models.es.administrative.model.OrganisationCompact;
import es.redmic.models.es.administrative.model.OrganisationContactRoles;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.PlatformTypeDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class PlatformESMapper extends CustomMapper<Platform, PlatformDTO> {

	@Autowired
	PlatformTypeESService platformTypeESService;

	@Autowired
	OrganisationESService organisationESService;

	@Override
	public void mapAtoB(Platform a, PlatformDTO b, MappingContext context) {

		if (a.getPlatformType() != null)
			b.setPlatformType(mapperFacade.map(a.getPlatformType(), PlatformTypeDTO.class));
		if (a.getOrganisation() != null)
			b.setOrganisation(mapperFacade.map(b.getOrganisation(), OrganisationDTO.class));
		if (a.getContacts() != null)
			b.setContacts(mapperFacade.mapAsList(a.getContacts(), ContactOrganisationRoleDTO.class));
	}

	@Override
	public void mapBtoA(PlatformDTO b, Platform a, MappingContext context) {

		a.setPlatformType((DomainES) mapperFacade.newObject(b.getPlatformType(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(platformTypeESService)));
		if (b.getOrganisation() != null)
			a.setOrganisation((OrganisationCompact) mapperFacade.map(mapperFacade.newObject(b.getOrganisation(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(organisationESService)), OrganisationCompact.class));
		a.setContacts(mapperFacade.mapAsList(b.getContacts(), OrganisationContactRoles.class));
	}
}
