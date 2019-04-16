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

import es.redmic.es.administrative.service.DocumentESService;
import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.maintenance.domain.administrative.repository.DocumentTypeESRepository;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.models.es.maintenance.administrative.dto.DocumentTypeDTO;

@Service
public class DocumentTypeESService extends MetaDataESService<DomainES, DocumentTypeDTO> {

	@Autowired
	DocumentESService documentESService;

	DocumentTypeESRepository repository;

	@Autowired
	public DocumentTypeESService(DocumentTypeESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		documentESService.updateDocumentType(reference);
	}

	public DocumentTypeDTO findByName(String name) {
		DataSearchWrapper<DomainES> result = repository.findByName(name);
		JSONCollectionDTO jsonCollect = orikaMapper.getMapperFacade().map(result.getHits(), JSONCollectionDTO.class,
				getMappingContext());

		if (jsonCollect == null || jsonCollect.getData() == null || jsonCollect.getData().size() != 1)
			throw new ItemNotFoundException("name", name);

		return (DocumentTypeDTO) jsonCollect.getData().get(0);
	}
}
