package es.redmic.es.maintenance.line.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.maintenance.classification.repository.ClassificationBaseESRepository;
import es.redmic.models.es.maintenance.line.dto.LineTypeDTO;
import es.redmic.models.es.maintenance.line.model.LineType;

@Repository
public class LineTypeESRepository extends ClassificationBaseESRepository<LineType, LineTypeDTO> {
	private static String[] INDEX = { "classification-domains" };
	private static String[] TYPE = { "linetype" };

	public LineTypeESRepository() {
		super(INDEX, TYPE);
	}
}