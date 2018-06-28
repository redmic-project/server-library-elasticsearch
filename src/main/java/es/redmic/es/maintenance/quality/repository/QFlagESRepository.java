package es.redmic.es.maintenance.quality.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.maintenance.quality.model.QFlag;

@Repository
public class QFlagESRepository extends RWDataESRepository<QFlag> {

	private static String[] INDEX = { "quality-domains" };
	private static String[] TYPE = { "qflag" };

	public QFlagESRepository() {
		super(INDEX, TYPE);
	}
}
