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

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.CountryESService;
import es.redmic.es.maintenance.domain.administrative.service.OrganisationTypeESService;
import es.redmic.models.es.administrative.dto.OrganisationDTO;
import es.redmic.models.es.administrative.model.Organisation;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.CountryDTO;
import es.redmic.models.es.maintenance.administrative.dto.OrganisationTypeDTO;
import es.redmic.models.es.maintenance.administrative.model.Country;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class OrganisationESMapper extends CustomMapper<Organisation, OrganisationDTO> {

	@Autowired
	OrganisationTypeESService organisationTypeESService;

	@Autowired
	CountryESService countryESService;

	@Override
	public void mapAtoB(Organisation a, OrganisationDTO b, MappingContext context) {

		b.setOrganisationType(mapperFacade.map(a.getOrganisationType(), OrganisationTypeDTO.class));
		if (a.getCountry() != null)
			b.setCountry(mapperFacade.map(a.getCountry(), CountryDTO.class));
	}

	@Override
	public void mapBtoA(OrganisationDTO b, Organisation a, MappingContext context) {

		a.setOrganisationType((DomainES) mapperFacade.newObject(b.getOrganisationType(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(organisationTypeESService)));
		
		if (b.getCountry() != null)
			a.setCountry((Country) mapperFacade.newObject(b.getCountry(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(countryESService)));
	}
}
