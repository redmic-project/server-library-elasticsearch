package es.redmic.es.maintenance.parameter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.parameter.repository.UnitTypeESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.parameter.dto.UnitTypeDTO;

@Service
public class UnitTypeESService extends DomainESService<DomainES, UnitTypeDTO> {

	@Autowired
	UnitESService unitESService;

	@Autowired
	public UnitTypeESService(UnitTypeESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		unitESService.updateUnitType(reference);
	}
}
