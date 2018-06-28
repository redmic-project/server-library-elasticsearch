package es.redmic.es.maintenance.samples.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.common.model.DomainES;

@Repository
public class SampleTypeESRepository extends DomainESRepository<DomainES> {
	private static String[] INDEX = { "parameter-domains" };
	private static String[] TYPE = { "sampletype" };

	public SampleTypeESRepository() {
		super(INDEX, TYPE);
	}
}