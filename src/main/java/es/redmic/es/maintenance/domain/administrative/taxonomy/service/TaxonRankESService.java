package es.redmic.es.maintenance.domain.administrative.taxonomy.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.TaxonRankESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.SortDTO;
import es.redmic.models.es.maintenance.taxonomy.dto.RankDTO;

@Service
public class TaxonRankESService extends DomainESService<DomainES, RankDTO> {

	@Autowired
	@Qualifier("TaxonServiceES")
	TaxonESService taxonESService;

	@Autowired
	public TaxonRankESService(TaxonRankESRepository repository) {
		super(repository);
	}

	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		taxonESService.updateTaxonRank(reference);
	}

	@SuppressWarnings("unchecked")
	public List<RankDTO> getRankClassification() {

		DataQueryDTO query = new DataQueryDTO();
		SortDTO sort = new SortDTO();
		sort.setField("id");
		sort.setOrder("ASC");
		query.setSorts(Arrays.asList(sort));

		return find(query).getData();
	}
}
