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
import es.redmic.es.maintenance.domain.administrative.service.DocumentTypeESService;
import es.redmic.models.es.administrative.dto.DocumentDTO;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.DocumentTypeDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class DocumentESMapper extends CustomMapper<Document, DocumentDTO> {

	@Autowired
	DocumentTypeESService documentTypeService;

	@Override
	public void mapAtoB(Document a, DocumentDTO b, MappingContext context) {

		if (a.getDocumentType() != null)
			b.setDocumentType(mapperFacade.map(a.getDocumentType(), DocumentTypeDTO.class));
	}

	@Override
	public void mapBtoA(DocumentDTO b, Document a, MappingContext context) {

		if (b.getDocumentType() != null) {
			a.setDocumentType((DomainES) mapperFacade.newObject(b.getDocumentType(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(documentTypeService)));
		}
	}
}
