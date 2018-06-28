package es.redmic.es.maintenance.device.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.common.model.DomainES;

@Repository
public class DeviceTypeESRepository extends DomainESRepository<DomainES> {
	private static String[] INDEX = { "parameter-domains" };
	private static String[] TYPE = { "devicetype" };

	public DeviceTypeESRepository() {
		super(INDEX, TYPE);
	}
}