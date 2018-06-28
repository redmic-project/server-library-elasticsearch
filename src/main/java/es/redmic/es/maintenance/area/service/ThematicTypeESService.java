package es.redmic.es.maintenance.area.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.HierarchicalESService;
import es.redmic.es.maintenance.area.repository.ThematicTypeESRepository;
import es.redmic.models.es.maintenance.areas.dto.ThematicTypeDTO;
import es.redmic.models.es.maintenance.areas.model.ThematicType;

@Service
public class ThematicTypeESService extends HierarchicalESService<ThematicType, ThematicTypeDTO>{

	@Autowired
	public ThematicTypeESService(ThematicTypeESRepository repository) {
		super(repository);
	}
}