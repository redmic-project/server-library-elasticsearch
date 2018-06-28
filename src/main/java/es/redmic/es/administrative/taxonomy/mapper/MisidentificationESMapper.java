package es.redmic.es.administrative.taxonomy.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.DocumentESService;
import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.geodata.citation.service.CitationESService;
import es.redmic.models.es.administrative.dto.DocumentDTO;
import es.redmic.models.es.administrative.model.DocumentCompact;
import es.redmic.models.es.administrative.taxonomy.dto.MisidentificationDTO;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.Misidentification;
import es.redmic.models.es.administrative.taxonomy.model.TaxonAncestorsCompact;
import es.redmic.models.es.geojson.citation.dto.CitationDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class MisidentificationESMapper extends CustomMapper<Misidentification, MisidentificationDTO> {

	@Autowired
	DocumentESService documentService;
	
	@Autowired
	@Qualifier("TaxonServiceES")
	private TaxonESService taxonESService;
	
	@Autowired
	private CitationESService citationService;

	@Override
	public void mapAtoB(Misidentification a, MisidentificationDTO b, MappingContext context) {
		
		b.setDocument(mapperFacade.map(a.getDocument(), DocumentDTO.class));
		b.setTaxon(mapperFacade.map(a.getTaxon(), TaxonDTO.class));
		b.setBadIdentity(mapperFacade.map(a.getBadIdentity(), TaxonDTO.class));
		b.setCitations(getCitationIdsByMisidentification(a.getId()));
	}

	@Override
	public void mapBtoA(MisidentificationDTO b, Misidentification a, MappingContext context) {

		a.setDocument(mapperFacade.map(mapperFacade.newObject(b.getDocument(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(documentService)), DocumentCompact.class));
		
		a.setTaxon(mapperFacade.map(mapperFacade.newObject(b.getTaxon(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(taxonESService)), TaxonAncestorsCompact.class));
		
		a.setBadIdentity(mapperFacade.map(mapperFacade.newObject(b.getBadIdentity(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(taxonESService)), TaxonAncestorsCompact.class));
	}

	private List<String> getCitationIdsByMisidentification(Long misidentificationId) {
		
		List<CitationDTO> result = citationService.findByMisidentification(String.valueOf(misidentificationId));
		List<String> citationIds = new ArrayList<String>();
		for (int i=0; i<result.size(); i++) {
			citationIds.add(result.get(i).getUuid());
		}
		return citationIds;
	}
}