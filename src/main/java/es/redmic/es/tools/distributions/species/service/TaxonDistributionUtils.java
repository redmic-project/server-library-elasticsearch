package es.redmic.es.tools.distributions.species.service;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Point;

import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.tools.distributions.species.repository.RWTaxonDistributionRepository;
import es.redmic.es.tools.distributions.species.repository.TaxonDist1000MRepository;
import es.redmic.es.tools.distributions.species.repository.TaxonDist100MRepository;
import es.redmic.es.tools.distributions.species.repository.TaxonDist5000MRepository;
import es.redmic.es.tools.distributions.species.repository.TaxonDist500MRepository;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.tools.distribution.model.Register;
import es.redmic.models.es.tools.distribution.species.model.TaxonDistribution;

@Service
public class TaxonDistributionUtils {

	@Autowired
	@Qualifier("TaxonServiceES")
	TaxonESService taxonESService;
	
	@Autowired
	TaxonDist100MRepository taxon100mRepository;
	
	@Autowired
	TaxonDist500MRepository taxon500mRepository;
	
	@Autowired
	TaxonDist1000MRepository taxon1000mRepository;
	
	@Autowired
	TaxonDist5000MRepository taxon5000mRepository;
	
	private Long highConfidence = 4L;
	
	@Autowired
	protected ObjectMapper objectMapper;
	
	public TaxonDistributionUtils() {
	}
	
	/**
	 * Función para obtener los repositorios donde debe estar un registro dependiendo del tamaño del grid.
	 * 
	 * @param gridSize tamaño del grid calculado a partir del radio del registro.
	 * 
	 * @return listado de repositorios donde debe estar el registro.
	 */
	
	public List<RWTaxonDistributionRepository> getRepoIncluded(String gridSize) {
		if (gridSize.equals("100"))
			return new ArrayList<RWTaxonDistributionRepository>(
					Arrays.asList(taxon100mRepository, taxon500mRepository, taxon1000mRepository, taxon5000mRepository));
		if (gridSize.equals("500"))
			return new ArrayList<RWTaxonDistributionRepository>(
					Arrays.asList(taxon500mRepository, taxon1000mRepository, taxon5000mRepository));
		if (gridSize.equals("1000"))
			return new ArrayList<RWTaxonDistributionRepository>(
					Arrays.asList(taxon1000mRepository, taxon5000mRepository));
		if (gridSize.equals("5000"))
			return new ArrayList<RWTaxonDistributionRepository>(
					Arrays.asList(taxon5000mRepository));
		//default
		return new ArrayList<RWTaxonDistributionRepository>(
				Arrays.asList(taxon100mRepository, taxon500mRepository, taxon1000mRepository, taxon5000mRepository));
	}
	
	/**
	 * Función para obtener los repositorios donde no debe estar un registro dependiendo del tamaño del grid.
	 * 
	 * @param gridSize tamaño del grid calculado a partir del radio del registro.
	 * 
	 * @return listado de repositorios donde no debe estar el registro.
	 */
	
	public List<RWTaxonDistributionRepository> getRepoExcluded(String gridSize) {
		if (gridSize.equals("100"))
			return new ArrayList<RWTaxonDistributionRepository>();
		if (gridSize.equals("500"))
			return new ArrayList<RWTaxonDistributionRepository>(
					Arrays.asList(taxon100mRepository));
		if (gridSize.equals("1000"))
			return new ArrayList<RWTaxonDistributionRepository>(
					Arrays.asList(taxon100mRepository, taxon500mRepository));
		if (gridSize.equals("5000"))
			return new ArrayList<RWTaxonDistributionRepository>(
					Arrays.asList(taxon100mRepository, taxon500mRepository, taxon1000mRepository));
		//default
		return new ArrayList<RWTaxonDistributionRepository>();
	}
	
	/**
	 * Función para obtener todos los repositorios de taxonDistribution.
	 * 
	 * @return listado de repositorios disponibles.
	 */
	
	public List<RWTaxonDistributionRepository> getAllRepo() {
		return new ArrayList<RWTaxonDistributionRepository>(
				Arrays.asList(taxon100mRepository, taxon500mRepository, taxon1000mRepository, taxon5000mRepository));
	}
	
	/**
	 * Función para obtener el modelo de taxonDistribution a partir de una cita.
	 * 
	 * @param model modelo de citation.
	 * 
	 * @return modelo de taxonDistribution.
	 */
	
	public TaxonDistribution getTaxonDistribution(Feature<GeoDataProperties, Point> model) {
		
		Long confidenceId = model.getProperties().getCollect().getConfidence().getId(),
				taxonId = model.getProperties().getCollect().getTaxon().getId();
		
		Taxon result = taxonESService.findById(String.valueOf(taxonId));
		
		String citationBaseUrl = 
				RWTaxonDistributionRepository.CITATION_BASE_URL.replace(RWTaxonDistributionRepository.ID_STR_REPLACE, model.get_parentId())+model.getUuid();
		
		
		if (model.getProperties().getCollect().getMisidentification() != null)
			return createTaxonDistribution(result, confidenceId, citationBaseUrl, model.getProperties().getCollect().getMisidentification().getTaxon().getPath());

		return createTaxonDistribution(result, confidenceId, citationBaseUrl);
	}
	
	
	/**
	 * Función para obtener el modelo de taxonDistribution a partir de un animalTracking.
	 * 
	 * @param model modelo de animalTracking.
	 * 
	 * @return modelo de taxonDistribution.
	 */
	
	public TaxonDistribution getAnimalDistribution(Feature<GeoDataProperties, Point> model) {
		
		Long taxonId = model.getProperties().getCollect().getTaxon().getId();
		Taxon taxon = taxonESService.findById(String.valueOf(taxonId));
		String animalTrackingBaseUrl = 
				RWTaxonDistributionRepository.ANIMAL_TRACKING_BASE_URL.replace(RWTaxonDistributionRepository.ID_STR_REPLACE, model.get_parentId())+model.getUuid();
		
		return createTaxonDistribution(taxon, highConfidence, animalTrackingBaseUrl);
	}
	
	
	private TaxonDistribution createTaxonDistribution(Taxon result, Long confidence, String baseUrl) {
		
		return  createTaxonDistribution(result, confidence, baseUrl, null);
	}
	

	private TaxonDistribution createTaxonDistribution(Taxon taxon, Long confidenceId, String baseUrl, String misidentification) {
		
		TaxonDistribution taxonDist = objectMapper.convertValue(taxon, TaxonDistribution.class);
		
		if (taxon.getValidAs() != null)
			taxonDist.setEquivalent(taxon.getValidAs().getPath());
		else
			taxonDist.setEquivalent(null);
	
		Register register = new Register();
		register.setConfidence(confidenceId);
		register.setMisidentification(misidentification);
		register.setId(baseUrl);
		taxonDist.setRegisters(new ArrayList<Register>(Arrays.asList(register)));
		
		return taxonDist;
	}
}
