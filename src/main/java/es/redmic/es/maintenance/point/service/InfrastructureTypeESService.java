package es.redmic.es.maintenance.point.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.HierarchicalESService;
import es.redmic.es.geodata.infrastructure.service.InfrastructureESService;
import es.redmic.es.maintenance.point.repository.InfrastructureTypeESRepository;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.point.dto.InfrastructureTypeDTO;
import es.redmic.models.es.maintenance.point.model.InfrastructureType;

@Service
public class InfrastructureTypeESService extends HierarchicalESService<InfrastructureType, InfrastructureTypeDTO> {

	@Autowired
	InfrastructureESService infrastructureESService;
	
	@Autowired
	public InfrastructureTypeESService(InfrastructureTypeESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<InfrastructureType> reference) {
		infrastructureESService.updateInfrastructureType(reference);
	}
}
