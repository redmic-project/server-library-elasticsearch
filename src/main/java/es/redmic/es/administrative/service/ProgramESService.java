package es.redmic.es.administrative.service;

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
