package es.redmic.es.maintenance.device.mapper;

/*-
 * #%L
 * ElasticSearch
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
