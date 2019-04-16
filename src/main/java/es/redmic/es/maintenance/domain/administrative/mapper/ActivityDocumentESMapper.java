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

import es.redmic.es.administrative.service.DocumentESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.models.es.administrative.dto.ActivityDocumentDTO;
import es.redmic.models.es.administrative.dto.DocumentCompactDTO;
import es.redmic.models.es.administrative.model.ActivityDocument;
import es.redmic.models.es.administrative.model.DocumentCompact;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ActivityDocumentESMapper extends CustomMapper<ActivityDocument, ActivityDocumentDTO> {

	@Autowired
	DocumentESService documentESService;

	@Override
	public void mapAtoB(ActivityDocument a, ActivityDocumentDTO b, MappingContext context) {

		b.setDocument(mapperFacade.map(a.getDocument(), DocumentCompactDTO.class));
	}

	@Override
	public void mapBtoA(ActivityDocumentDTO b, ActivityDocument a, MappingContext context) {

		a.setDocument(mapperFacade.map(mapperFacade.newObject(b.getDocument(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(documentESService)), DocumentCompact.class));
	}
}
