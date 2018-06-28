package es.redmic.es.maintenance.point.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.maintenance.classification.repository.ClassificationBaseESRepository;
import es.redmic.models.es.maintenance.point.dto.InfrastructureTypeDTO;
import es.redmic.models.es.maintenance.point.model.InfrastructureType;

@Repository
public class InfrastructureTypeESRepository extends ClassificationBaseESRepository<InfrastructureType, InfrastructureTypeDTO> {

	private static String[] INDEX = { "classification-domains" };
	private static String[] TYPE = { "infrastructuretype" };
	
	public InfrastructureTypeESRepository() {
		super(INDEX, TYPE);
	}
}
