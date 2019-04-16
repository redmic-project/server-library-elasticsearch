package es.redmic.es.geodata.infrastructure.service;

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

import es.redmic.es.geodata.common.service.GeoDataESService;
import es.redmic.es.geodata.infrastructure.repository.InfrastructureESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.infrastructure.dto.InfrastructureDTO;
import es.redmic.models.es.maintenance.point.model.InfrastructureType;

@Service
public class InfrastructureESService extends GeoDataESService<InfrastructureDTO, GeoPointData> {

	/*Índica si las referencias a actualizar son de tipo anidadas*/
	private static Boolean isNestedProperty = false;

	/*Path de elastic para buscar por InfrastructureType*/
	private String infrastructureTypePropertyPath = "properties.infrastructureType.id";
	/*Clase del modelo indexado en la referencia*/
	private static Class<DomainES> infrastructurePointClassInReference = DomainES.class;

	@Autowired
	public InfrastructureESService(InfrastructureESRepository repository) {
		super(repository);
	}

	/**
	 * Función que debe estar definida en los servicios específicos.
	 * Se usa para rellenar el modelo de elastic a apartir del dto.
	 *
	 * @param dtoToIndex
	 *            Dto con la información a guardar.
	 *
	 * @return Modelo que se va a indexar en elastic.
	 */

	@Override
	public GeoPointData mapper(InfrastructureDTO dtoToIndex) {

		return orikaMapper.getMapperFacade().map(dtoToIndex, GeoPointData.class);
	}
	
	/**
	 * Función para modificar las referencias de infrastructurePointType en
	 * infrastructure en caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de infrastructurePointType antes
	 *            y después de ser modificado.
	 */

	public void updateInfrastructureType(ReferencesES<InfrastructureType> reference) {

		updateReferenceByScript(reference, infrastructurePointClassInReference, infrastructureTypePropertyPath, isNestedProperty);
	}

	/*
	 * Función que debe estar definida en los servicios específicos.
	 * Se usa para realizar acciones en otros servicios después de modificar.
	 */
	@Override
	protected void postUpdate(ReferencesES<GeoPointData> reference) {}

	/*
	 *  Función que debe estar definida en los servicios específicos. 
	 *  Se usa para realizar acciones en otros servicios después de añadir.
	 */
	@Override
	protected void postSave(Object model) {}

	/*
	 * Función que debe estar definida en los servicios para tener el item antes de borrarlo.
	 */
	@Override
	protected void preDelete(Object object) {}

	
	/*
	 * Función que debe estar definida en los servicios específicos.
	 * Se usa para realizar acciones en otros servicios después de borrar.
	 */
	@Override
	protected void postDelete(String id) {}
}
