package es.redmic.es.administrative.taxonomy.service;

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
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.taxonomy.repository.AnimalESRepository;
import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.geodata.tracking.animal.service.AnimalTrackingESService;
import es.redmic.models.es.administrative.taxonomy.dto.AnimalDTO;
import es.redmic.models.es.administrative.taxonomy.model.Animal;
import es.redmic.models.es.administrative.taxonomy.model.Species;
import es.redmic.models.es.administrative.taxonomy.model.TaxonValid;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;

@Service
public class AnimalESService extends MetaDataESService<Animal, AnimalDTO> {

	@Autowired
	AnimalTrackingESService animalTrackingESService;

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> lifeStageClassInReference = DomainES.class;
	/* Path de elastic para buscar por lifeStage */
	private String lifeStagePropertyPath = "lifeStage.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> sexClassInReference = DomainES.class;
	/* Path de elastic para buscar por sex */
	private String sexPropertyPath = "sex.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<TaxonValid> taxonClassInReference = TaxonValid.class;
	/* Path de elastic para buscar por taxon */
	private String taxonPropertyPath = "taxon.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> endingClassInReference = DomainES.class;
	/* Path de elastic para buscar por ending */
	private String endingPropertyPath = "recoveries.ending.id";
	/*
	 * Profundida de anidamiento de ending con respecto al array de recoveries
	 * (por defecto 1)
	 */
	private int nestingDepthOfEnding = 2;

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> destinyClassInReference = DomainES.class;
	/* Path de elastic para buscar por ending */
	private String destinyPropertyPath = "recoveries.destiny.id";
	/*
	 * Profundida de anidamiento de destiny con respecto al array de recoveries
	 * (por defecto 1)
	 */
	private int nestingDepthOfDestiny = 2;

	AnimalESRepository repository;

	@Autowired
	public AnimalESService(AnimalESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	public AnimalDTO findByUuid(String uuid) {
		return repository.findByUuid(uuid);
	}

	/**
	 * Función para modificar las referencias de lifeStage en animal en caso de
	 * ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de lifeStage antes y después de
	 *            ser modificado.
	 */

	public void updateLifeStage(ReferencesES<DomainES> reference) {

		updateReference(reference, lifeStageClassInReference, lifeStagePropertyPath);
	}

	/**
	 * Función para modificar las referencias de sex en animal en caso de ser
	 * necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de sex antes y después de ser
	 *            modificado.
	 */

	public void updateSex(ReferencesES<DomainES> reference) {

		updateReference(reference, sexClassInReference, sexPropertyPath);
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

		updateReference(reference, taxonClassInReference, taxonPropertyPath);
	}

	/**
	 * Función para modificar las referencias de Ending en recoveries de
	 * aninamal en caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de ending antes y después de ser
	 *            modificado.
	 */

	public void updateEnding(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, endingClassInReference, endingPropertyPath, nestingDepthOfEnding);
	}

	/**
	 * Función para modificar las referencias de Destiny en recoveries de
	 * aninamal en caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de destiny antes y después de
	 *            ser modificado.
	 */

	public void updateDestiny(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, destinyClassInReference, destinyPropertyPath, nestingDepthOfDestiny);
	}

	@Override
	public void postUpdate(ReferencesES<Animal> reference) {
		animalTrackingESService.updateAnimal(reference);
	}
}
