package es.redmic.es.administrative.service;

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

import es.redmic.es.administrative.repository.ProgramESRepository;
import es.redmic.es.atlas.service.LayerESService;
import es.redmic.models.es.administrative.dto.ProgramDTO;
import es.redmic.models.es.administrative.model.Program;
import es.redmic.models.es.common.model.ReferencesES;

@Service
public class ProgramESService extends ActivityBaseAbstractESService<Program, ProgramDTO> {

	private String rankId = "1";

	@Autowired
	private LayerESService layerESService;

	@Autowired
	public ProgramESService(ProgramESRepository repository) {
		super(repository);
	}

	@Override
	public Program mapper(ProgramDTO dtoToIndex) {
		Program model = super.mapper(dtoToIndex);

		model.setRank(getRank(getRankId()));
		return model;
	}

	@Override
	public void postUpdate(ReferencesES<Program> reference) {

		layerESService.updateActivity(reference);
	}

	public String getRankId() {
		return rankId;
	}

	public void setRankId(String rankId) {
		this.rankId = rankId;
	}
}
