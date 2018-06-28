package es.redmic.es.maintenance.qualifiers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.HierarchicalESService;
import es.redmic.es.maintenance.qualifiers.repository.AttributeTypeESRepository;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.qualifiers.dto.AttributeTypeDTO;
import es.redmic.models.es.maintenance.qualifiers.model.AttributeType;

@Service
public class AttributeTypeESService extends HierarchicalESService<AttributeType, AttributeTypeDTO> {

	@Autowired
	public AttributeTypeESService(AttributeTypeESRepository repository) {
		super(repository);
	}

	@Override
	public void postUpdate(ReferencesES<AttributeType> reference) {

		super.postUpdate(reference);
	}
}
