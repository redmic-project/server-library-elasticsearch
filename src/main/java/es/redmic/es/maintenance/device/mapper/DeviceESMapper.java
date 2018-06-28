package es.redmic.es.maintenance.device.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.DocumentESService;
import es.redmic.es.administrative.service.PlatformESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.device.service.DeviceTypeESService;
import es.redmic.models.es.administrative.dto.DocumentDTO;
import es.redmic.models.es.administrative.dto.PlatformDTO;
import es.redmic.models.es.administrative.model.DocumentCompact;
import es.redmic.models.es.administrative.model.PlatformCompact;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.device.dto.CalibrationDTO;
import es.redmic.models.es.maintenance.device.dto.DeviceDTO;
import es.redmic.models.es.maintenance.device.dto.DeviceTypeDTO;
import es.redmic.models.es.maintenance.device.model.Calibration;
import es.redmic.models.es.maintenance.device.model.Device;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class DeviceESMapper extends CustomMapper<Device, DeviceDTO> {

	@Autowired
	DeviceTypeESService deviceTypeESService;

	@Autowired
	PlatformESService platformESService;

	@Autowired
	DocumentESService documentESService;

	@Override
	public void mapAtoB(Device a, DeviceDTO b, MappingContext context) {

		b.setDeviceType(mapperFacade.map(a.getDeviceType(), DeviceTypeDTO.class));
		
		if (a.getPlatform() != null)
			b.setPlatform(mapperFacade.map(a.getPlatform(), PlatformDTO.class));
		
		if (a.getDocument() != null)
			b.setDocument(mapperFacade.map(a.getDocument(), DocumentDTO.class));
		
		b.setCalibrations(mapperFacade.mapAsList(a.getCalibrations(), CalibrationDTO.class));
	}

	@Override
	public void mapBtoA(DeviceDTO b, Device a, MappingContext context) {

		a.setDeviceType((DomainES) mapperFacade.newObject(b.getDeviceType(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(deviceTypeESService)));
		
		if (b.getPlatform() != null)
			a.setPlatform(mapperFacade.map(mapperFacade.newObject(b.getPlatform(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(platformESService)), PlatformCompact.class));
		
		if (b.getDocument() != null)
			a.setDocument(mapperFacade.map(mapperFacade.newObject(b.getDocument(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(documentESService)), DocumentCompact.class));
		
		a.setCalibrations(mapperFacade.mapAsList(b.getCalibrations(), Calibration.class));
	}
}
