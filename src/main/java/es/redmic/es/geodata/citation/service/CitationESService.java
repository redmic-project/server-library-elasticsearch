package es.redmic.es.geodata.citation.service;

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

import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import es.redmic.es.geodata.citation.repository.CitationESRepository;
import es.redmic.es.geodata.common.service.GeoPresenceESService;
import es.redmic.es.tools.distributions.species.service.RWTaxonDistributionService;
import es.redmic.models.es.administrative.taxonomy.model.Misidentification;
import es.redmic.models.es.administrative.taxonomy.model.MisidentificationCompact;
import es.redmic.models.es.administrative.taxonomy.model.Species;
import es.redmic.models.es.administrative.taxonomy.model.TaxonValid;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.geojson.citation.dto.CitationDTO;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;

@Service
public class CitationESService extends GeoPresenceESService<CitationDTO, GeoPointData> {

	CitationESRepository repository;

	@Autowired
	@Qualifier("RWTaxonDistributionService")
	RWTaxonDistributionService taxonDistributionService;

	/* Índica si las referencias a actualizar son de tipo anidadas */
	private static Boolean isNestedProperty = false;

	/* Path de elastic para buscar por misidentification */
	private String misidentificationPropertyPath = "properties.collect.misidentification.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<MisidentificationCompact> misidentificationClassInReference = MisidentificationCompact.class;

	/* Path de elastic para buscar por taxon */
	private String taxonPropertyPath = "properties.collect.taxon.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<TaxonValid> taxonClassInReference = TaxonValid.class;

	/* Path de elastic para buscar por accessibility */
	private String speciesConfidencePropertyPath = "properties.collect.confidence.id";

	/* Path de elastic para buscar por accessibility */
	private String confidencePropertyPath = "properties.collect.localityConfidence.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> confidenceClassInReference = DomainES.class;

	@Autowired
	public CitationESService(CitationESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	/**
	 * Función para rellenar el modelo de elastic a apartir del dto.
	 *
	 * @param dtoToIndex
	 *            Dto con la información a guardar.
	 *
	 * @return Modelo que se va a indexar en elastic.
	 */

	@Override
	public GeoPointData mapper(CitationDTO dtoToIndex) {

		return orikaMapper.getMapperFacade().map(dtoToIndex, GeoPointData.class);
	}

	public GeoJSONFeatureCollectionDTO findByDocument(DataQueryDTO queryDTO, String documentId) {

		GeoSearchWrapper<GeoDataProperties, Point> result = repository.findByDocument(queryDTO, documentId);

		return orikaMapper.getMapperFacade().map(result.getHits(), GeoJSONFeatureCollectionDTO.class,
				getMappingContext());
	}

	public List<CitationDTO> findByMisidentification(String mistidentificationId) {

		GeoSearchWrapper<GeoDataProperties, Point> result = repository.findByMisidentification(mistidentificationId);

		return orikaMapper.getMapperFacade().mapAsList(result.getHits().getHits(), CitationDTO.class,
				getMappingContext());
	}

	/**
	 * Función para modificar las referencias de misidentification en citation
	 * en caso de ser necesario.
	 *
	 * @param reference
	 *            clase que encapsula el modelo de mistidentification antes y
	 *            después de ser modificado.
	 */

	public void updateMisidentification(ReferencesES<Misidentification> reference) {

		updateReferenceByScript(reference, misidentificationClassInReference, misidentificationPropertyPath,
				isNestedProperty);
	}

	/**
	 * Función para modificar las referencias de taxon en citation en caso de
	 * ser necesario.
	 *
	 * @param reference
	 *            clase que encapsula el modelo de taxon antes y después de ser
	 *            modificado.
	 */

	public void updateTaxon(ReferencesES<Species> reference) {

		updateReferenceByScript(reference, taxonClassInReference, taxonPropertyPath, isNestedProperty);
	}

	/**
	 * Función para modificar las referencias de confidence en citation en caso
	 * de ser necesario.
	 *
	 * @param reference
	 *            clase que encapsula el modelo de confidence antes y después de
	 *            ser modificado.
	 */

	public void updateConfidence(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, confidenceClassInReference, confidencePropertyPath, isNestedProperty);

		updateReferenceByScript(reference, confidenceClassInReference, speciesConfidencePropertyPath, isNestedProperty);
	}

	/*
	 * Función que debe estar definida en los servicios específicos. Se usa para
	 * realizar acciones en otros servicios después de modificar.
	 */
	@Override
	protected void postUpdate(ReferencesES<GeoPointData> reference) {
		taxonDistributionService.updateCitation(reference);
	}

	/*
	 * Función que debe estar definida en los servicios específicos. Se usa para
	 * realizar acciones en otros servicios después de añadir.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void postSave(Object model) {
		taxonDistributionService.addCitation((Feature<GeoDataProperties, Point>) model);
	}

	/*
	 * Función que debe estar definida en los servicios para tener el item antes
	 * de borrarlo.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void preDelete(Object model) {
		taxonDistributionService.deleteCitation((Feature<GeoDataProperties, Point>) model);
	}

	/*
	 * Función que debe estar definida en los servicios específicos. Se usa para
	 * realizar acciones en otros servicios después de borrar.
	 */
	@Override
	protected void postDelete(String id) {
	}
}
