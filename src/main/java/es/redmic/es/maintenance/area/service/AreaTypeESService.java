package es.redmic.es.maintenance.area.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.area.repository.AreaTypeESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.areas.dto.AreaTypeDTO;

@Service
public class AreaTypeESService extends DomainESService<DomainES, AreaTypeDTO> {

	@Autowired
	public AreaTypeESService(AreaTypeESRepository repository) {
		super(repository);
	}

	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		// TODO: postupdate
	}
}
