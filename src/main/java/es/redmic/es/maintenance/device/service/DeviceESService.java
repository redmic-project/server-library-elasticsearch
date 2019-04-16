package es.redmic.es.maintenance.device.service;

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

import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.geodata.geofixedstation.service.GeoFixedTimeSeriesESService;
import es.redmic.es.geodata.tracking.animal.service.AnimalTrackingESService;
import es.redmic.es.maintenance.device.repository.DeviceESRepository;
import es.redmic.models.es.administrative.model.Contact;
import es.redmic.models.es.administrative.model.ContactCompact;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.administrative.model.DocumentCompact;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.administrative.model.PlatformCompact;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.device.dto.DeviceDTO;
import es.redmic.models.es.maintenance.device.model.Device;

@Service
public class DeviceESService extends MetaDataESService<Device, DeviceDTO> {

	@Autowired
	GeoFixedTimeSeriesESService geoFixedTimeSeriesESService;

	@Autowired
	AnimalTrackingESService animalTrackingESService;

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> deviceTypeClassInReference = DomainES.class;
	/* Path de elastic para buscar por deviceType */
	private String deviceTypePropertyPath = "deviceType.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<PlatformCompact> platformClassInReference = PlatformCompact.class;
	/* Path de elastic para buscar por platform */
	private String platformPropertyPath = "platform.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DocumentCompact> documentClassInReference = DocumentCompact.class;
	/* Path de elastic para buscar por document */
	private String documentPropertyPath = "document.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<ContactCompact> contactClassInReference = ContactCompact.class;
	/* Path de elastic para buscar por contact */
	private String contactPropertyPath = "calibrations.contact.id";

	@Autowired
	public DeviceESService(DeviceESRepository repository) {
		super(repository);
	}

	/**
	 * Función para modificar las referencias de deviceType en device en caso de
	 * ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de deviceType antes y después de
	 *            ser modificado.
	 */

	public void updateDeviceType(ReferencesES<DomainES> reference) {

		updateReference(reference, deviceTypeClassInReference, deviceTypePropertyPath);
	}

	/**
	 * Función para modificar las referencias de platform en device en caso de
	 * ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de platform antes y después de
	 *            ser modificado.
	 */

	public void updatePlatform(ReferencesES<Platform> reference) {

		updateReference(reference, platformClassInReference, platformPropertyPath);
	}

	/**
	 * Función para modificar las referencias de document en device en caso de
	 * ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de document antes y después de
	 *            ser modificado.
	 */

	public void updateDocument(ReferencesES<Document> reference) {

		updateReference(reference, documentClassInReference, documentPropertyPath);
	}

	/**
	 * Función para modificar las referencias de contact en calibrations en caso
	 * de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de contact antes y después de
	 *            ser modificado.
	 */

	public void updateContact(ReferencesES<Contact> reference) {

		updateReferenceByScript(reference, contactClassInReference, contactPropertyPath, 2);
	}

	@Override
	public void postUpdate(ReferencesES<Device> reference) {

		animalTrackingESService.updateDevice(reference);
		geoFixedTimeSeriesESService.updateDevice(reference);
	}
}
