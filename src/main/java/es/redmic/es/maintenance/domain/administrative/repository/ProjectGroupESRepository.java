package es.redmic.es.maintenance.domain.administrative.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.common.model.DomainES;

@Repository
public class ProjectGroupESRepository extends DomainESRepository<DomainES> {
	private static String[] INDEX = { "administrative-domains" };
	private static String[] TYPE = { "projectgroup" };

	public ProjectGroupESRepository() {
		super(INDEX, TYPE);
	}
}
