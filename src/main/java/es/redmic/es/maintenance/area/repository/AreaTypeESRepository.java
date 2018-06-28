package es.redmic.es.maintenance.area.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.common.model.DomainES;

@Repository
public class AreaTypeESRepository extends DomainESRepository<DomainES> {

	private static String[] INDEX = { "classification-domains" };
	private static String[] TYPE = { "areatype" };

	public AreaTypeESRepository() {
		super(INDEX, TYPE);
	}
}
