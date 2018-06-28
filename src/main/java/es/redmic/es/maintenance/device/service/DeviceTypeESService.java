package es.redmic.es.maintenance.device.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.device.repository.DeviceTypeESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.device.dto.DeviceTypeDTO;

@Service
public class DeviceTypeESService extends DomainESService<DomainES, DeviceTypeDTO> {

	@Autowired
	DeviceESService deviceESService;

	@Autowired
	public DeviceTypeESService(DeviceTypeESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		deviceESService.updateDeviceType(reference);
	}
}
