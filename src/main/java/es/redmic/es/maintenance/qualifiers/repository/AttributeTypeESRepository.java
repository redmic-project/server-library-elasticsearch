package es.redmic.es.maintenance.qualifiers.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.maintenance.classification.repository.ClassificationBaseESRepository;
import es.redmic.models.es.maintenance.qualifiers.dto.AttributeTypeDTO;
import es.redmic.models.es.maintenance.qualifiers.model.AttributeType;

@Repository
public class AttributeTypeESRepository extends ClassificationBaseESRepository<AttributeType, AttributeTypeDTO> {

	private static String[] INDEX = { "parameter-domains" };
	private static String[] TYPE = { "attributetype" };

	public AttributeTypeESRepository() {
		super(INDEX, TYPE);
	}
}