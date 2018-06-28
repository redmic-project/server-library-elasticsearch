package es.redmic.es.administrative.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.OrganisationESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.models.es.administrative.dto.ContactDTO;
import es.redmic.models.es.administrative.dto.OrganisationDTO;
import es.redmic.models.es.administrative.model.Contact;
import es.redmic.models.es.administrative.model.OrganisationCompact;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ContactESMapper extends CustomMapper<Contact, ContactDTO> {

	@Autowired
	OrganisationESService organisationESService;

	@Override
	public void mapAtoB(Contact a, ContactDTO b, MappingContext context) {

		b.setAffiliation(mapperFacade.map(a.getAffiliation(), OrganisationDTO.class));
	}

	@Override
	public void mapBtoA(ContactDTO b, Contact a, MappingContext context) {

		a.setAffiliation(mapperFacade.map(mapperFacade.newObject(b.getAffiliation(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(organisationESService)), OrganisationCompact.class));
	}
}
