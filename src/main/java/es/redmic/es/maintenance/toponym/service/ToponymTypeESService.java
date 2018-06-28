package es.redmic.es.maintenance.toponym.service;

import org.springframework.stereotype.Service;

import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.toponym.repository.ToponymTypeESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.toponym.dto.ToponymTypeDTO;

@Service
public class ToponymTypeESService extends DomainESService<DomainES, ToponymTypeDTO> {

	public ToponymTypeESService(ToponymTypeESRepository repository) {
		super(repository);
	}

	@Override
	protected void postUpdate(ReferencesES<DomainES> reference) {
		// TODO Auto-generated method stub
		
	}
}
