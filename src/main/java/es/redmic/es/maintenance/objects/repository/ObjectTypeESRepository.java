package es.redmic.es.maintenance.objects.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.maintenance.classification.repository.ClassificationBaseESRepository;
import es.redmic.models.es.maintenance.objects.dto.ObjectTypeDTO;
import es.redmic.models.es.maintenance.objects.model.ObjectType;

@Repository
public class ObjectTypeESRepository extends ClassificationBaseESRepository<ObjectType, ObjectTypeDTO> {
	private static String[] INDEX = { "parameter-domains" };
	private static String[] TYPE = { "objecttype" };

	public ObjectTypeESRepository() {
		super(INDEX, TYPE);
	}
}