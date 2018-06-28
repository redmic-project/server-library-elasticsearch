package es.redmic.es.maintenance.statistics.service;

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
