package es.redmic.es.geodata.citation.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.taxonomy.service.MisidentificationESService;
import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.ConfidenceESService;
import es.redmic.models.es.administrative.taxonomy.dto.MisidentificationDTO;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.MisidentificationCompact;
import es.redmic.models.es.administrative.taxonomy.model.TaxonValid;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.geojson.citation.dto.CitationPropertiesDTO;
import es.redmic.models.es.geojson.common.domain.dto.ConfidenceDTO;
import es.redmic.models.es.geojson.properties.model.Collect;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class CitationPropertiesESMapper extends CustomMapper<GeoDataProperties, CitationPropertiesDTO> {

	@Autowired
	@Qualifier("TaxonServiceES")
	public TaxonESService taxonService;

	@Autowired
	public ConfidenceESService confidenceService;

	@Autowired
	public MisidentificationESService misidentificationService;

	@Override
	public void mapAtoB(GeoDataProperties a, CitationPropertiesDTO b, MappingContext context) {

		b.setZ(a.getCollect().getZ());
		b.setDeviation(a.getCollect().getDeviation());
		b.setRadius(a.getCollect().getRadius());

		b.setRemark(a.getCollect().getRemark());
		b.setCollectorName(a.getCollect().getCollectorName());
		
		if (a.getCollect().getValue() != null)
			b.setSpecimenCount(a.getCollect().getValue().longValue());

		b.setStartDate(a.getCollect().getStartDate());
		b.setEndDate(a.getCollect().getEndDate());
		
		b.setUpdated(a.getUpdated());

		b.setTaxon((TaxonDTO) mapperFacade.map(a.getCollect().getTaxon(), TaxonDTO.class));
		b.setMisidentification((MisidentificationDTO) mapperFacade.map(a.getCollect().getMisidentification(),
				MisidentificationDTO.class));
		b.setSpeciesConfidence(mapperFacade.map(a.getCollect().getConfidence(), ConfidenceDTO.class));
		b.setConfidence(mapperFacade.map(a.getCollect().getLocalityConfidence(), ConfidenceDTO.class));
	}

	@Override
	public void mapBtoA(CitationPropertiesDTO b, GeoDataProperties a, MappingContext context) {

		Collect collect = new Collect();

		collect.setZ(b.getZ());
		collect.setDeviation(b.getDeviation());
		collect.setRadius(b.getRadius());
		collect.setRemark(b.getRemark());
		collect.setCollectorName(b.getCollectorName());
		
		if (b.getSpecimenCount() != null)
			collect.setValue(b.getSpecimenCount().doubleValue());
		
		collect.setStartDate(b.getStartDate());
		collect.setEndDate(b.getEndDate());;
		
		collect.setTaxon((TaxonValid) mapperFacade.map(mapperFacade.newObject(b.getTaxon(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(taxonService)), TaxonValid.class));
		
		if (b.getMisidentification() != null)
			collect.setMisidentification((MisidentificationCompact) mapperFacade.map(mapperFacade.newObject(b.getMisidentification(),
					DataMapperUtils.getBaseType(), DataMapperUtils.getObjectFactoryContext(misidentificationService)), MisidentificationCompact.class));
		
		if (b.getSpeciesConfidence() != null)
			collect.setConfidence((DomainES) mapperFacade.newObject(b.getSpeciesConfidence(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(confidenceService)));
	
		if (b.getConfidence() != null)
			collect.setLocalityConfidence((DomainES) mapperFacade.newObject(b.getConfidence(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(confidenceService)));

		collect.setqFlag('1');
		collect.setvFlag('A');

		a.setCollect(collect);
		a.setUpdated(a.getUpdated());
	}
}
