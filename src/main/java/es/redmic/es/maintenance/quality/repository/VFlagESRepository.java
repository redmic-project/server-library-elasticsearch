package es.redmic.es.maintenance.quality.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.maintenance.quality.model.VFlag;

@Repository
public class VFlagESRepository extends RWDataESRepository<VFlag> {

	private static String[] INDEX = { "quality-domains" };
	private static String[] TYPE = { "vflag" };

	public VFlagESRepository() {
		super(INDEX, TYPE);
	}
}
