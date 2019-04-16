package es.redmic.es.toponym.service;

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

import es.redmic.es.geodata.common.repository.RWGeoDataESRepository;
import es.redmic.es.geodata.common.service.RWGeoDataESService;
import es.redmic.es.toponym.repository.ToponymESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.geojson.toponym.dto.ToponymDTO;
import es.redmic.models.es.geojson.toponym.model.Toponym;

@Service
public class ToponymESService extends RWGeoDataESService<ToponymDTO, Toponym> {
	
	@Autowired
	public ToponymESService(ToponymESRepository repository) {
		super((RWGeoDataESRepository<Toponym>) repository);
	}
	
	/**
	 * Función para rellenar el modelo de elastic a apartir del dto.
	 *
	 * @param dtoToIndex
	 *            Dto con la información a guardar.
	 *
	 * @return Modelo que se va a indexar en elastic.
	 */
	
	public Toponym mapper(ToponymDTO dtoToIndex) {
		
		return orikaMapper.getMapperFacade().map(dtoToIndex, Toponym.class);
	}

	/*
	 * Función que debe estar definida en los servicios específicos.
	 * Se usa para realizar acciones en otros servicios después de modificar.
	 */
	protected void postUpdate(ReferencesES<Toponym> reference) {
	}

	
	protected void postSave(Object model) {
	}

	/*
	 * Función que debe estar definida en los servicios para tener el item antes de borrarlo.
	 */
	protected void preDelete(Object model) {
	}

	/*
	 * Función que debe estar definida en los servicios específicos.
	 * Se usa para realizar acciones en otros servicios después de borrar.
	 */
	protected void postDelete(String id) {}
	
	public void updateToponymType(ReferencesES<DomainES> reference) {
		// TODO: Llamar a updateReference
	}
}
