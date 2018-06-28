package es.redmic.es.maintenance.domain.administrative.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.service.OrganisationESService;
import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.maintenance.domain.administrative.repository.CountryESRepository;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.administrative.dto.CountryDTO;
import es.redmic.models.es.maintenance.administrative.model.Country;

@Service
public class CountryESService extends MetaDataESService<Country, CountryDTO> {

	@Autowired
	OrganisationESService organisationESService;

	@Autowired
	public CountryESService(CountryESRepository repository) {
		super(repository);
	}
	
	@Override
	public void postUpdate(ReferencesES<Country> reference) {
		organisationESService.updateCountry(reference);
	}
}
