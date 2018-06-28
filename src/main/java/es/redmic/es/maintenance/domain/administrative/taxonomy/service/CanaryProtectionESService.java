package es.redmic.es.maintenance.domain.administrative.taxonomy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.taxonomy.service.SpeciesESService;
import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.CanaryProtectionESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.taxonomy.dto.CanaryProtectionDTO;

@Service
public class CanaryProtectionESService extends DomainESService<DomainES, CanaryProtectionDTO> {

	@Autowired
	SpeciesESService speciesESService;

	@Autowired
	public CanaryProtectionESService(CanaryProtectionESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		speciesESService.updateCanaryProtection(reference);
	}
}
