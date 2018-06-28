package es.redmic.es.administrative.taxonomy.service;

import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.taxonomy.repository.TaxonESRepository;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonBaseDTO;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.common.dto.DTOEvent;

@Service(value = "TaxonServiceES")
public class TaxonESService extends TaxonBaseESService<Taxon, TaxonDTO> {

	@Autowired
	public TaxonESService(TaxonESRepository taxonRepository) {
		super(taxonRepository);
	}

	@Override
	public Boolean chkEventIsMine(Type typeOfTDTO, DTOEvent event) {

		return (event.getDto() instanceof TaxonBaseDTO) && chkIsMyRank((TaxonBaseDTO) event.getDto());
	}
}
