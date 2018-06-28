package es.redmic.es.administrative.taxonomy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.taxonomy.repository.FilumESRepository;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;

@Service
public class FilumESService extends TaxonBaseESService<Taxon, TaxonDTO> {

	private String rankId = "3";

	@Autowired
	public FilumESService(FilumESRepository repository) {
		super(repository);
	}

	@Override
	public String getRankId() {
		return rankId;
	}

	@Override
	public void setRankId(String rankId) {
		this.rankId = rankId;
	}
}
