package es.redmic.es.maintenance.domain.administrative.taxonomy.service;

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

import es.redmic.es.common.service.DomainESService;
import es.redmic.es.geodata.citation.service.CitationESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.ConfidenceESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.geojson.common.domain.dto.ConfidenceDTO;

@Service
public class ConfidenceESService extends DomainESService<DomainES, ConfidenceDTO> {

	@Autowired
	CitationESService citationESService;

	@Autowired
	public ConfidenceESService(ConfidenceESRepository repository) {
		super(repository);
	}

	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {

		citationESService.updateConfidence(reference);
		//objectCollectingSeriesESService.updateConfidence(reference);
	}
}
