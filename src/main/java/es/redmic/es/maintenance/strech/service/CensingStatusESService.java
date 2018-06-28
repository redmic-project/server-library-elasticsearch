package es.redmic.es.maintenance.strech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.strech.repository.CensingStatusESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.strech.dto.CensingStatusDTO;

@Service
public class CensingStatusESService extends DomainESService<DomainES, CensingStatusDTO> {

	@Autowired
	public CensingStatusESService(CensingStatusESRepository repository) {
		super(repository);
	}

	@Override
	protected void postUpdate(ReferencesES<DomainES> reference) {
		// TODO: postUpdate
	}
}
