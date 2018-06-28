package es.redmic.es.maintenance.area.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.maintenance.classification.repository.ClassificationBaseESRepository;
import es.redmic.models.es.maintenance.areas.dto.ThematicTypeDTO;
import es.redmic.models.es.maintenance.areas.model.ThematicType;

@Repository
public class ThematicTypeESRepository extends ClassificationBaseESRepository<ThematicType, ThematicTypeDTO> {
	private static String[] INDEX = { "classification-domains" };
	private static String[] TYPE = { "thematictype" };

	public ThematicTypeESRepository() {
		super(INDEX, TYPE);
	}
}