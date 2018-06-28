package es.redmic.es.maintenance.samples.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.samples.repository.SampleTypeESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.samples.dto.SampleTypeDTO;

@Service
public class SampleTypeESService extends DomainESService<DomainES, SampleTypeDTO> {

	@Autowired
	public SampleTypeESService(SampleTypeESRepository repository) {
		super(repository);
	}

	@Override
	protected void postUpdate(ReferencesES<DomainES> reference) {
		// TODO: postUpdate	
	}
}
