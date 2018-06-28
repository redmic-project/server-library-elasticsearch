package es.redmic.es.administrative.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.CountryESService;
import es.redmic.es.maintenance.domain.administrative.service.OrganisationTypeESService;
import es.redmic.models.es.administrative.dto.OrganisationDTO;
import es.redmic.models.es.administrative.model.Organisation;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.CountryDTO;
import es.redmic.models.es.maintenance.administrative.dto.OrganisationTypeDTO;
import es.redmic.models.es.maintenance.administrative.model.Country;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class OrganisationESMapper extends CustomMapper<Organisation, OrganisationDTO> {

	@Autowired
	OrganisationTypeESService organisationTypeESService;

	@Autowired
	CountryESService countryESService;

	@Override
	public void mapAtoB(Organisation a, OrganisationDTO b, MappingContext context) {

		b.setOrganisationType(mapperFacade.map(a.getOrganisationType(), OrganisationTypeDTO.class));
		if (a.getCountry() != null)
			b.setCountry(mapperFacade.map(a.getCountry(), CountryDTO.class));
	}

	@Override
	public void mapBtoA(OrganisationDTO b, Organisation a, MappingContext context) {

		a.setOrganisationType((DomainES) mapperFacade.newObject(b.getOrganisationType(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(organisationTypeESService)));
		
		if (b.getCountry() != null)
			a.setCountry((Country) mapperFacade.newObject(b.getCountry(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(countryESService)));
	}
}
