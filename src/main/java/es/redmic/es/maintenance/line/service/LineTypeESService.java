package es.redmic.es.maintenance.line.service;

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

import es.redmic.es.common.service.HierarchicalESService;
import es.redmic.es.maintenance.line.repository.LineTypeESRepository;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.line.dto.LineTypeDTO;
import es.redmic.models.es.maintenance.line.model.LineType;

@Service
public class LineTypeESService extends HierarchicalESService<LineType, LineTypeDTO> {

	@Autowired
	public LineTypeESService(LineTypeESRepository repository) {
		super(repository);
	}

	@Override
	protected void postUpdate(ReferencesES<LineType> reference) {
		// TODO: postUpdate	
	}
}
