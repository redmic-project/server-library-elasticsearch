package es.redmic.es.maintenance.parameter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.parameter.repository.ParameterTypeESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.parameter.dto.ParameterTypeDTO;

@Service
public class ParameterTypeESService extends DomainESService<DomainES, ParameterTypeDTO> {

	@Autowired
	ParameterESService parameterESService;

	@Autowired
	public ParameterTypeESService(ParameterTypeESRepository repository) {
		super(repository);
	}

	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		parameterESService.updateParameterType(reference);
	}
}
