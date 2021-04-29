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

import java.util.Arrays;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.geodata.common.service.GridServiceItfc;
import es.redmic.es.tools.distributions.species.repository.RWTaxonDistributionRepository;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.tools.distribution.model.Distribution;
import es.redmic.models.es.tools.distribution.species.model.TaxonDistribution;

@Service("RWTaxonDistributionService")
public class RWTaxonDistributionService extends RTaxonDistributionService {

	/* Nombre de script para eliminar registros en taxonDistribution */
	private String removeRegisterFromDistributionScript = "remove-register-distribution";

	/* Nombre de script para modificar registros en taxonDistribution */
	private String updateRegisterFromDistributionScript = "update-register-distribution";

	/* Nombre de script para añadir registros en taxonDistribution */
	private String addRegisterFromDistributionScript = "add-register-distribution";

	private static List<String> citationFieldsInReference = Arrays.asList(new String[] { "properties.collect.radius",
			"properties.collect.taxon.id", "properties.collect.taxon.authorship", "properties.collect.taxon.path",
			"properties.collect.taxon.scientificName", "properties.collect.taxon.validAs.id",
			"properties.collect.confidence.id", "properties.collect.misidentification",
			"properties.collect.misidentification.taxon.id",
			"properties.collect.misidentification.taxon.scientificName",
			"properties.collect.misidentification.taxon.authorship", "properties.collect.misidentification.taxon.path",
			"geometry.coordinates", "geometry" });

	private static List<String> animalTrackingFieldsInReference = Arrays
			.asList(new String[] { "properties.inTrack.qFlag", "properties.collect.radius",
					"properties.collect.taxon.id", "properties.collect.taxon.authorship",
					"properties.collect.taxon.path", "properties.collect.taxon.scientificName",
					"properties.collect.taxon.validAs.id", "properties.collect.animal.taxon.id",
					"properties.collect.animal.taxon.authorship", "properties.collect.animal.taxon.path",
					"properties.collect.animal.taxon.scientificName", "properties.collect.animal.taxon.validAs",
					"properties.collect.animal.taxon.validAs.id", "geometry.coordinates", "geometry" });

	private static List<String> geometryInReference = Arrays
			.asList(new String[] { "properties.collect.radius", "geometry.coordinates", "geometry" });

	@Autowired
	public RWTaxonDistributionService(GridServiceItfc gridUtil) {
		super(new RWTaxonDistributionRepository(), gridUtil);
		this.gridUtil = gridUtil;

	}

	public RWTaxonDistributionService(RWTaxonDistributionRepository repository, GridServiceItfc gridUtil) {
		super(repository, gridUtil);
		this.repository = repository;
		this.gridUtil = gridUtil;
	}

	/**
	 * Función para modificar las referencias de citation en su repositorio en
	 * caso de ser necesario.
	 *
	 * @param reference
	 *            clase que encapsula el modelo de citation antes y después de
	 *            ser modificado.
	 */

	public void updateCitation(ReferencesES<GeoPointData> reference) {

		if (needUpdateRegister(reference, citationFieldsInReference)) {

			GeoPointData register = reference.getNewModel();
			String citationUrl = RWTaxonDistributionRepository.CITATION_BASE_URL.replace(
					RWTaxonDistributionRepository.ID_STR_REPLACE, register.get_parentId()) + register.getUuid();

			TaxonDistribution distributionModel = taxonDistributionUtils.getTaxonDistribution(register);
			// Si ha cambiado las coordenadas o el radio que afectan a la
			// geometría se recalcula las rejillas
			if (needUpdateRegister(reference, geometryInReference)) {
				updateReferenceAndGeometry(register.getId(), register.getProperties().getCollect().getRadius(),
						register.getGeometry(), citationUrl, distributionModel);
			} else { // solo se modifica los metadatos
				updateReference(register.getId(), register.getProperties().getCollect().getRadius(),
						register.getGeometry(), citationUrl, distributionModel);
			}
		}
	}

	/**
	 * Función para añadir una referencia de citation en su repositorio en caso
	 * de ser necesario.
	 *
	 * @param register
	 *            modelo de citation para ser añadido.
	 */

	public void addCitation(Feature<GeoDataProperties, Point> register) {

		String gridSize = Precision.getGridSize(register.getProperties().getCollect().getRadius());
		if (gridSize != null) {
			addRegister(taxonDistributionUtils.getTaxonDistribution(register),
					taxonDistributionUtils.getRepoIncluded(gridSize), register.getGeometry(),
					register.getProperties().getCollect().getRadius().intValue());
		}
	}

	/**
	 * Función para eliminar una referencia de citation en su repositorio en
	 * caso de ser necesario.
	 *
	 * @param id
	 *            identificador de citation para ser eliminado.
	 */

	public void deleteCitation(Feature<GeoDataProperties, Point> feature) {

		deleteRegister(RWTaxonDistributionRepository.CITATION_PATH + feature.getUuid(),
				taxonDistributionUtils.getAllRepo());
	}

	/**
	 * Función para modificar las referencias de animalTracking en su
	 * repositorio en caso de ser necesario.
	 *
	 * @param reference
	 *            clase que encapsula el modelo de animalTracking antes y
	 *            después de ser modificado.
	 */

	public void updateAnimalTracking(ReferencesES<GeoPointData> reference) {

		GeoPointData register = reference.getNewModel();
		String animalTrackingBaseUrl = RWTaxonDistributionRepository.ANIMAL_TRACKING_BASE_URL
				.replace(RWTaxonDistributionRepository.ID_STR_REPLACE, register.get_parentId()) + register.getUuid();
		// Si ha cambiado algún dato de los indexados y el dato es válido
		if (needUpdateRegister(reference, animalTrackingFieldsInReference)
				&& reference.getNewModel().getProperties().getCollect().getqFlag().equals(qFlagHigh)) {

			TaxonDistribution distributionModel = taxonDistributionUtils.getAnimalDistribution(register);

			// Si ha cambiado las coordenadas y/o el radio que afectan a la
			// geometría o cambia la validez del dato a correcto, se recalcula
			// las rejillas.
			if (needUpdateRegister(reference, geometryInReference)
					|| (reference.getNewModel().getProperties().getCollect().getqFlag().equals(qFlagHigh)
							&& !reference.getOldModel().getProperties().getCollect().getqFlag().equals(qFlagHigh))) {

				updateReferenceAndGeometry(register.getId(), register.getProperties().getCollect().getRadius(),
						register.getGeometry(), animalTrackingBaseUrl, distributionModel);
			}
			// si es válido solo se modifica los metadatos
			else if (reference.getNewModel().getProperties().getCollect().getqFlag().equals(qFlagHigh)
					&& reference.getNewModel().getProperties().getCollect().getqFlag()
							.equals(reference.getOldModel().getProperties().getCollect().getqFlag())) {

				updateReference(register.getId(), register.getProperties().getCollect().getRadius(),
						register.getGeometry(), animalTrackingBaseUrl, distributionModel);
			}
			// si no es válido eliminar
			else if (!reference.getNewModel().getProperties().getCollect().getqFlag().equals(qFlagHigh)
					&& reference.getOldModel().getProperties().getCollect().getqFlag().equals(qFlagHigh)) {

				deleteRegister(animalTrackingBaseUrl + register.getId(), taxonDistributionUtils.getAllRepo());
			}
		}
		// si no es válido eliminar
		else if (needUpdateRegister(reference, animalTrackingFieldsInReference)
				&& !register.getProperties().getCollect().getqFlag().equals(qFlagHigh)) {

			deleteRegister(animalTrackingBaseUrl + register.getId(), taxonDistributionUtils.getAllRepo());
		}
	}

	/**
	 * Función para añadir una referencia de animalTracking en su repositorio en
	 * caso de ser necesario.
	 *
	 * @param register
	 *            modelo de animalTracking para ser añadido.
	 */

	public void addAnimalTracking(Feature<GeoDataProperties, Point> register) {

		String gridSize = Precision.getGridSize(register.getProperties().getCollect().getRadius());
		if (gridSize != null) {
			addRegister(taxonDistributionUtils.getAnimalDistribution(register),
					taxonDistributionUtils.getRepoIncluded(gridSize), register.getGeometry(),
					register.getProperties().getCollect().getRadius().intValue());
		}
	}

	/**
	 * Función para eliminar una referencia de animalTracking en su repositorio
	 * en caso de ser necesario.
	 *
	 * @param id
	 *            identificador de animalTracking para ser eliminado.
	 */

	public void deleteAnimalTracking(Feature<GeoDataProperties, Point> feature) {

		deleteRegister(RWTaxonDistributionRepository.ANIMAL_TRACKING_PATH + feature.getUuid(),
				taxonDistributionUtils.getAllRepo());
	}

	/**
	 * Función genérica para modificar las referencias de animalTracking |
	 * citation... en su repositorio en caso de solo cambie campos que no
	 * afecten al cálculo de las rejillas.
	 *
	 * @param id
	 *            identificador de la referencia.
	 * @param radius
	 *            radio de precisión del registro.
	 * @param geometry
	 *            geometría del registro.
	 * @param baseUrl
	 *            identificador del registro (url base del registro + id)
	 * @param distributionModel
	 *            modelo de distribution para añadir.
	 */

	private void updateReference(Long id, Double radius, Geometry geometry, String registerId,
			TaxonDistribution distributionModel) {

		String gridSize = Precision.getGridSize(radius);
		if (gridSize != null) {

			List<RWTaxonDistributionRepository> reposIncluded = taxonDistributionUtils.getRepoIncluded(gridSize);

			for (int i = 0; i < reposIncluded.size(); i++) {

				TaxonDistribution source = reposIncluded.get(i).findByRegisterId(registerId);
				if (source != null) {

					String oldGridId = String.valueOf(source.getId());
					updateRegister(oldGridId, distributionModel, reposIncluded.get(i));
				}
				/*
				 * else { addRegister(distributionModel, reposIncluded.get(i),
				 * geometry, radius.intValue()); }
				 */
			}
		}
	}

	/**
	 * Función genérica para modificar las referencias de animalTracking |
	 * citation... en su repositorio en caso de que cambie las coordenadas y/o
	 * el radio.
	 *
	 * @param id
	 *            identificador de la referencia.
	 * @param radius
	 *            radio de precisión del registro.
	 * @param geometry
	 *            geometría del registro.
	 * @param baseUrl
	 *            identificador del registro (url base del registro + id)
	 * @param distributionModel
	 *            modelo de distribution para añadir.
	 */

	private void updateReferenceAndGeometry(Long id, Double radius, Geometry geometry, String registerId,
			TaxonDistribution distributionModel) {

		String gridSize = Precision.getGridSize(radius);

		if (gridSize != null) {

			List<RWTaxonDistributionRepository> reposIncluded = taxonDistributionUtils.getRepoIncluded(gridSize);

			// Añade en los repositorios donde debe estar)
			for (int i = 0; i < reposIncluded.size(); i++) {

				TaxonDistribution source = reposIncluded.get(i).findByRegisterId(registerId);
				if (source != null) {
					// Calculo geometr�a por si cambi� el radio
					Distribution distributionInGrid = gridUtil.getDistribution(geometry, reposIncluded.get(i),
							radius.intValue());

					String oldGridId = String.valueOf(source.getId());

					if (distributionInGrid != null) {

						String newGridId = String.valueOf(distributionInGrid.getId());

						// Si cae en el mismo grid modificamos
						if (newGridId.equals(oldGridId))
							updateRegister(oldGridId, distributionModel, reposIncluded.get(i));
						else { // Si no, borramos el registro anterior y
								// a�adimos el nuevo en el grid adecuado

							deleteRegister(registerId, reposIncluded.get(i));
							addRegister(distributionModel, reposIncluded.get(i), geometry, radius.intValue());
						}
					} else { // si no existe la rejilla, elimina el registro
						deleteRegister(registerId, reposIncluded.get(i));
					}
				} else {
					addRegister(distributionModel, reposIncluded.get(i), geometry, radius.intValue());
				}
			}

			// Borrar en los repositorios donde no debe estar)
			deleteRegister(registerId, taxonDistributionUtils.getRepoExcluded(gridSize));
		} else {
			deleteRegister(registerId, taxonDistributionUtils.getAllRepo());
		}
	}

	/**
	 * Función para añadir un registro en los repositorios de taxonDistribution
	 * pasados.
	 *
	 * @param distributionModel
	 *            modelo de distribution para añadir.
	 * @param repos
	 *            repositorios donde que quiere añadir.
	 * @param geometry
	 *            geometría del registro.
	 * @param radius
	 *            radio de precisión del registro.
	 */

	private void addRegister(TaxonDistribution distributionModel, List<RWTaxonDistributionRepository> repos,
			Geometry geometry, Integer radius) {

		for (int i = 0; i < repos.size(); i++) {
			addRegister(distributionModel, repos.get(i), geometry, radius);
		}
	}

	/**
	 * Función para añadir un registro en el repositorio de taxonDistribution.
	 *
	 * @param distributionModel
	 *            modelo de distribution para añadir.
	 * @param repository
	 *            repositorio donde que quiere añadir.
	 * @param point
	 *            geometría del registro.
	 * @param radius
	 *            radio de precisión del registro.
	 */

	private void addRegister(TaxonDistribution distributionModel, RWTaxonDistributionRepository repository,
			Geometry geometry, Integer radius) {

		Distribution distributionInGrid = gridUtil.getDistribution(geometry, repository, radius);

		if (distributionInGrid != null) {
			repository.addRegister(distributionModel, distributionInGrid, addRegisterFromDistributionScript);
		}
	}

	/**
	 * Función para modificar un registro en el repositorio de taxonDistribution
	 * pasado.
	 *
	 * @param id
	 *            identificador del registro
	 * @param distributionModel
	 *            modelo de distribution actualizado.
	 * @param repository
	 *            repositorio donde que quiere añadir.
	 */

	private void updateRegister(String id, TaxonDistribution distributionModel,
			RWTaxonDistributionRepository repository) {

		repository.updateRegister(id, distributionModel, updateRegisterFromDistributionScript);
	}

	/**
	 * Función para eliminar un registro en los repositorios de
	 * taxonDistribution pasados.
	 *
	 * @param id
	 *            identificador del registro (url base del registro + id)
	 * @param repos
	 *            repositorios donde que quiere eliminar.
	 */

	private void deleteRegister(String id, List<RWTaxonDistributionRepository> repositories) {

		for (int i = 0; i < repositories.size(); i++)
			deleteRegister(id, repositories.get(i));
	}

	/**
	 * Función para eliminar un registro en el repositorio de taxonDistribution
	 * pasado.
	 *
	 * @param id
	 *            identificador del registro (url base del registro + id)
	 * @param repos
	 *            repositorio donde que quiere eliminar.
	 */

	private void deleteRegister(String id, RWTaxonDistributionRepository repository) {

		repository.deleteRegister(id, removeRegisterFromDistributionScript);
	}

	/**
	 * Función para comprobar si la referencia a citation | animalTracking es
	 * necesaria actualizarla (especial para distribution).
	 *
	 * @param reference
	 *            clase que encapsula el modelo de citation antes y después de
	 *            ser modificado.
	 * @param registerFieldsInReference
	 *            lista de parámetros de animalTracking indexados en
	 *            taxonDistribution.
	 * @return True si es necesario actualizar, False en caso contrario.
	 */

	private Boolean needUpdateRegister(ReferencesES<?> reference, List<String> registerFieldsInReference) {

		reference.setFieldsInReference(registerFieldsInReference);
		for (int i = 0; i < registerFieldsInReference.size(); i++)
			if (reference.getDifferences().contains(registerFieldsInReference.get(i)))
				return true;
		return false;
	}
}
