package es.redmic.es.maintenance.parameter.service;

import org.springframework.beans.factory.annotation.Autowired;

import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.maintenance.parameter.repository.MetricDefinitionESRepository;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.parameter.dto.MetricDefinitionDTO;
import es.redmic.models.es.maintenance.parameter.model.MetricDefinition;

//@Service
public class MetricDefinitionESService extends MetaDataESService<MetricDefinition, MetricDefinitionDTO> {

	@Autowired
	public MetricDefinitionESService(MetricDefinitionESRepository repository) {
		super(repository);
	}

	@Override
	protected void postUpdate(ReferencesES<MetricDefinition> reference) {
		// TODO hacer postupdate
		
	}
}