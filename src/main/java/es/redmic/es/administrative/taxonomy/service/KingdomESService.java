package es.redmic.es.administrative.taxonomy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.taxonomy.repository.KingdomESRepository;
import es.redmic.models.es.administrative.taxonomy.dto.KingdomDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;

@Service
public class KingdomESService extends TaxonBaseESService<Taxon, KingdomDTO> {

	private String rankId = "1";

	@Autowired
	public KingdomESService(KingdomESRepository repository) {
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
