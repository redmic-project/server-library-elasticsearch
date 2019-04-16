package es.redmic.es.toponym.mapper;

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
import es.redmic.es.maintenance.toponym.service.ToponymTypeESService;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.geojson.toponym.dto.ToponymPropertiesDTO;
import es.redmic.models.es.geojson.toponym.model.ToponymProperties;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ToponymPropertiesESMapper extends CustomMapper<ToponymProperties, ToponymPropertiesDTO> {

	@Autowired
	public ToponymTypeESService toponymTypeESService;

	/*@Override
	public void mapAtoB(ToponymProperties a, ToponymPropertiesDTO b, MappingContext context) {

		b.setTaxon((TaxonDTO) mapperFacade.map(a.getCollect().getTaxon(), TaxonDTO.class));
	
	}*/

	@Override
	public void mapBtoA(ToponymPropertiesDTO b, ToponymProperties a, MappingContext context) {
		
		if (b.getToponymType() != null)
			a.setToponymType((DomainES) mapperFacade.newObject(b.getToponymType(),
					DataMapperUtils.getBaseType(), DataMapperUtils.getObjectFactoryContext(toponymTypeESService)));
	}
}
