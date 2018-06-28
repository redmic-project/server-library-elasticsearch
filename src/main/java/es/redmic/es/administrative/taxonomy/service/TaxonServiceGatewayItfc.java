package es.redmic.es.administrative.taxonomy.service;

import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;

public interface TaxonServiceGatewayItfc {

	public <T> T save(TaxonDTO taxon);

	public <T> T update(TaxonDTO taxon);
}
