package es.redmic.es.maintenance.device.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.maintenance.device.model.Device;

@Repository
public class DeviceESRepository extends RWDataESRepository<Device> {
	private static String[] INDEX = { "administrative" };
	private static String[] TYPE = { "device" };

	public DeviceESRepository() {
		super(INDEX, TYPE);
	}
}