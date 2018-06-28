package es.redmic.es.administrative.taxonomy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.taxonomy.repository.SubfilumESRepository;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;

@Service
public class SubfilumESService extends TaxonBaseESService<Taxon, TaxonDTO> {

	private String rankId = "5";

	@Autowired
	public SubfilumESService(SubfilumESRepository repository) {
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
