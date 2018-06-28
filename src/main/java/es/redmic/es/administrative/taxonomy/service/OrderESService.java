package es.redmic.es.administrative.taxonomy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.taxonomy.repository.OrderESRepository;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;

@Service
public class OrderESService extends TaxonBaseESService<Taxon, TaxonDTO> {

	private String rankId = "7";

	@Autowired
	public OrderESService(OrderESRepository repository) {
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
