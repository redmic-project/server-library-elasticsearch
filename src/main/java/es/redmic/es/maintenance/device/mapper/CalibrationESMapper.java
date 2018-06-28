package es.redmic.es.maintenance.device.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.ContactESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.models.es.administrative.dto.ContactDTO;
import es.redmic.models.es.administrative.model.ContactCompact;
import es.redmic.models.es.maintenance.device.dto.CalibrationDTO;
import es.redmic.models.es.maintenance.device.model.Calibration;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class CalibrationESMapper extends CustomMapper<Calibration, CalibrationDTO> {

	@Autowired
	ContactESService contactService;

	@Override
	public void mapAtoB(Calibration a, CalibrationDTO b, MappingContext context) {

		if (a.getContact() != null)
			b.setContact(mapperFacade.map(a.getContact(), ContactDTO.class));
	}

	@Override
	public void mapBtoA(CalibrationDTO b, Calibration a, MappingContext context) {

		if (b.getContact() != null)
			a.setContact((ContactCompact) mapperFacade.newObject(b.getContact(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(contactService)));
	}
}
