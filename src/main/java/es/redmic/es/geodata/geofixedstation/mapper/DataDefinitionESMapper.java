package es.redmic.es.geodata.geofixedstation.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.ContactESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.device.service.DeviceESService;
import es.redmic.es.maintenance.domain.administrative.service.ContactRoleESService;
import es.redmic.models.es.administrative.dto.ContactDTO;
import es.redmic.models.es.administrative.model.ContactCompact;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.RoleDTO;
import es.redmic.models.es.maintenance.device.dto.DeviceDTO;
import es.redmic.models.es.maintenance.device.model.DeviceCompact;
import es.redmic.models.es.maintenance.parameter.dto.DataDefinitionDTO;
import es.redmic.models.es.maintenance.parameter.model.DataDefinition;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class DataDefinitionESMapper extends CustomMapper<DataDefinition, DataDefinitionDTO> {

	@Autowired
	DeviceESService deviceESService;
	
	@Autowired
	ContactESService contactService;
	
	@Autowired
	ContactRoleESService contactRoleService;

	@Override
	public void mapBtoA(DataDefinitionDTO b, DataDefinition a, MappingContext context) {
		
		if(b.getDevice() != null) {
			a.setDevice((DeviceCompact) mapperFacade.map(mapperFacade.newObject(b.getDevice(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(deviceESService)),  DeviceCompact.class));
		}
		if (b.getContact() != null) {
			a.setContact((ContactCompact) mapperFacade.map(mapperFacade.newObject(b.getContact(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(contactService)), ContactCompact.class));
		}
		if (b.getContactRole() != null) {
			a.setContactRole((DomainES) mapperFacade.newObject(b.getContactRole(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(contactRoleService)));
		}
	}
	
	@Override
	public void mapAtoB(DataDefinition a, DataDefinitionDTO b, MappingContext context) {
		
		b.setDevice(mapperFacade.map(a.getDevice(), DeviceDTO.class));
		b.setContact(mapperFacade.map(a.getContact(), ContactDTO.class));
		b.setContactRole(mapperFacade.map(a.getContactRole(), RoleDTO.class));
	}
}
