package es.redmic.es.maintenance.domain.administrative.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.service.PlatformESService;
import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.domain.administrative.repository.PlatformTypeESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.administrative.dto.PlatformTypeDTO;

@Service
public class PlatformTypeESService extends DomainESService<DomainES, PlatformTypeDTO> {

	@Autowired
	PlatformESService platformESService;

	@Autowired
	public PlatformTypeESService(PlatformTypeESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		platformESService.updatePlatformType(reference);
	}
}
