package es.redmic.es.maintenance.parameter.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.maintenance.parameter.model.Unit;

@Repository
public class UnitESRepository extends RWDataESRepository<Unit> {

	private static String[] INDEX = { "parameter-domains" };
	private static String[] TYPE = { "unit" };

	public UnitESRepository() {
		super(INDEX, TYPE);
	}
}
