package es.redmic.es.maintenance.statistics.service;

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

import es.redmic.es.maintenance.statistics.repository.StatisticsESRepository;
import es.redmic.models.es.maintenance.statistics.dto.AdministrativeStatisticsDTO;

@Service
public class StatisticsESService {

	private StatisticsESRepository repository;

	@Autowired
	public StatisticsESService(StatisticsESRepository repository) {
		this.repository = repository;
	}

	public AdministrativeStatisticsDTO administrativeStatistics() {

		AdministrativeStatisticsDTO dto = new AdministrativeStatisticsDTO();

		dto.setProgram(repository.programsStatistics());
		dto.setProject(repository.projectsStatistics());
		dto.setProjectOutProgram(repository.projectOutProgramStatistics());
		dto.setActivity(repository.activitiesStatistics());
		dto.setActivityOutProject(repository.activityOutProjectStatistics());

		dto.setContact(repository.contactsStatistics());
		dto.setPlatform(repository.platformsStatistics());
		dto.setDocument(repository.documentsStatistics());
		dto.setOrganisation(repository.organisationStatistics());
		return dto;
	}
}
