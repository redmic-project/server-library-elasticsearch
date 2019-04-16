package es.redmic.es.geodata.tracking.platform.service;

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

import es.redmic.es.geodata.common.service.GeoPresenceESService;
import es.redmic.es.geodata.tracking.platform.repository.PlatformTrackingESRepository;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.administrative.model.PlatformCompact;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.tracking.platform.dto.PlatformTrackingDTO;
import es.redmic.models.es.maintenance.device.model.Device;
import es.redmic.models.es.maintenance.device.model.DeviceCompact;

@Service
public class PlatformTrackingESService extends GeoPresenceESService<PlatformTrackingDTO, GeoPointData> {
	
	PlatformTrackingESRepository repository;
	
	// Índica si las referencias a actualizar son de tipo anidadas
	private static Boolean isNestedProperty = false;
	
	// Path de elastic para buscar por animal
	private String platformPropertyPath = "properties.inTrack.platform.id";
	
	// Clase del modelo indexado en la referencia
	private static Class<PlatformCompact> plaftormClassInReference = PlatformCompact.class;
	
	// Path de elastic para buscar por anima
	private String devicePropertyPath = "properties.inTrack.device.id";
	
	// Clase del modelo indexado en la referencia
	private static Class<DeviceCompact> deviceClassInReference = DeviceCompact.class;

	@Autowired
	public PlatformTrackingESService(PlatformTrackingESRepository repository) {
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
	public GeoPointData mapper(PlatformTrackingDTO dtoToIndex) {
		
		return orikaMapper.getMapperFacade().map(dtoToIndex, GeoPointData.class);
	}
	
	/**
	 * Función para modificar las referencias de animal en animalTracking en caso de ser necesario.
	 * 
	 * @param reference clase que encapsula el modelo de animal antes y después de ser modificado.
	 */

	public void updatePlatform(ReferencesES<Platform> reference) {
		
		updateReferenceByScript(reference, plaftormClassInReference, platformPropertyPath, isNestedProperty);
	}
	
	/**
	 * Función para modificar las referencias de device en animalTracking en caso de ser necesario.
	 * 
	 * @param reference clase que encapsula el modelo de device antes y después de ser modificado.
	 */

	public void updateDevice(ReferencesES<Device> reference) {
		
		updateReferenceByScript(reference, deviceClassInReference, devicePropertyPath, isNestedProperty);
	}

	@Override
	protected void postUpdate(ReferencesES<GeoPointData> reference) {}

	@Override
	protected void postSave(Object model) {}

	@Override
	protected void preDelete(Object object) {}

	@Override
	protected void postDelete(String id) {
		
	}

}
