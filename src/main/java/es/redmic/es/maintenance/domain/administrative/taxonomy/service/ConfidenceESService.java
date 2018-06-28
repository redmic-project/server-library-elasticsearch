package es.redmic.es.maintenance.domain.administrative.taxonomy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.DomainESService;
import es.redmic.es.geodata.citation.service.CitationESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.ConfidenceESRepository;
import es.redmic.es.series.objectcollecting.service.ObjectCollectingSeriesESService;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.geojson.common.domain.dto.ConfidenceDTO;

@Service
public class ConfidenceESService extends DomainESService<DomainES, ConfidenceDTO> {

	@Autowired
	CitationESService citationESService;
	
	@Autowired
	ObjectCollectingSeriesESService objectCollectingSeriesESService;

	@Autowired
	public ConfidenceESService(ConfidenceESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		
		citationESService.updateConfidence(reference);
		objectCollectingSeriesESService.updateConfidence(reference);
	}
}
