package es.redmic.es.maintenance.line.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.HierarchicalESService;
import es.redmic.es.maintenance.line.repository.LineTypeESRepository;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.line.dto.LineTypeDTO;
import es.redmic.models.es.maintenance.line.model.LineType;

@Service
public class LineTypeESService extends HierarchicalESService<LineType, LineTypeDTO> {

	@Autowired
	public LineTypeESService(LineTypeESRepository repository) {
		super(repository);
	}

	@Override
	protected void postUpdate(ReferencesES<LineType> reference) {
		// TODO: postUpdate	
	}
}
