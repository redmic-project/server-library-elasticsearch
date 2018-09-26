package es.redmic.es.administrative.taxonomy.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.maintenance.domain.administrative.taxonomy.service.StatusESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.TaxonRankESService;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.dto.WormsDTO;
import es.redmic.models.es.maintenance.taxonomy.dto.RankDTO;
import es.redmic.models.es.maintenance.taxonomy.dto.StatusDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class WormsESMapper extends CustomMapper<WormsDTO, TaxonDTO> {

	@Autowired
	StatusESService statusESService;

	@Autowired
	TaxonRankESService rankESService;

	@Override
	public void mapAtoB(WormsDTO a, TaxonDTO b, MappingContext context) {
		super.mapAtoB(a, b, context);

		b.setAuthorship(a.getAuthority());
		b.setScientificName(a.getScientificname());
		b.setWorms(a.getUrl());
		b.setStatus(mapperFacade.map(statusESService.findByName_en(a.getStatus()), StatusDTO.class));
		b.setRank(mapperFacade.map(rankESService.findByName_en(a.getRank()), RankDTO.class));

		b.setWormsUpdated(a.getModified());
	}
}