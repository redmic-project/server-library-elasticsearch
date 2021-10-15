package es.redmic.es.geodata.geofixedstation.service;

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

import es.redmic.es.geodata.geofixedstation.repository.GeoFixedTimeSeriesESRepository;
import es.redmic.models.es.administrative.model.Contact;
import es.redmic.models.es.administrative.model.ContactCompact;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.geofixedstation.dto.GeoFixedTimeSeriesDTO;
import es.redmic.models.es.maintenance.device.model.Device;
import es.redmic.models.es.maintenance.device.model.DeviceCompact;
import es.redmic.models.es.maintenance.parameter.model.Parameter;
import es.redmic.models.es.maintenance.parameter.model.Unit;
import ma.glasnost.orika.MappingContext;

@Service
public class GeoFixedTimeSeriesESService extends GeoFixedBaseESService<GeoFixedTimeSeriesDTO, GeoPointData> {

	/* Path de elastic para buscar por contact */
	private String contactPropertyPath = "properties.measurements.dataDefinition.contact.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<ContactCompact> contactClassInReference = ContactCompact.class;


	/* Path de elastic para buscar por contact */
	private String devicePropertyPath = "properties.measurements.dataDefinition.device.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<DeviceCompact> deviceClassInReference = DeviceCompact.class;


	/* Path de elastic para buscar por parameter */
	private String parameterPropertyPath = "properties.measurements.parameter.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<Parameter> parameterClassInReference = Parameter.class;

	/* Path de elastic para buscar por unit */
	private String unitPropertyPath = "properties.measurements.unit.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<Unit> unitClassInReference = Unit.class;

	/* Path de elastic para buscar por contactRole */
	private String contactRolePropertyPath = "properties.measurements.dataDefinition.contactRole.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> contactRoleClassInReference = DomainES.class;

	/*
	 * Profundidad de anidamiento de campos en data definition de measurement
	 */
	private int nestingDepthOfDataDefinitionProperty = 3;

	/*
	 * Profundidad de anidamiento de campos en measurement
	 */
	private int nestingDepthOfMeasurementProperty = 2;

	@Autowired
	public GeoFixedTimeSeriesESService(GeoFixedTimeSeriesESRepository repository) {
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
	public GeoPointData mapper(GeoFixedTimeSeriesDTO dtoToIndex) {

		MappingContext context = orikaMapper.getMappingContext();
		context.setProperty("uuid", dtoToIndex.getUuid());
		context.setProperty("geoDataPrefix", DataPrefixType.FIXED_TIMESERIES);

		GeoPointData model = orikaMapper.getMapperFacade().map(dtoToIndex, GeoPointData.class, context);
		if (dtoToIndex.getProperties() != null)
			model.getProperties().setActivityId(dtoToIndex.getProperties().getActivityId());
		return model;
	}

	/**
	 * Función para modificar las referencias de contact en surveyparams en caso
	 * de ser necesario.
	 *
	 * @param reference
	 *            clase que encapsula el modelo de contact antes y después de
	 *            ser modificado.
	 */

	public void updateContact(ReferencesES<Contact> reference) {

		updateReferenceByScript(reference, contactClassInReference, contactPropertyPath, nestingDepthOfDataDefinitionProperty, true);
	}

	/**
	 * Función para modificar las referencias de device en surveyparams en caso
	 * de ser necesario.
	 *
	 * @param reference
	 *            clase que encapsula el modelo de device antes y después de ser
	 *            modificado.
	 */

	public void updateDevice(ReferencesES<Device> reference) {

		updateReferenceByScript(reference, deviceClassInReference, devicePropertyPath, nestingDepthOfDataDefinitionProperty, true);
	}

	/**
	 * Función para modificar las referencias de parameter en surveyparams en
	 * caso de ser necesario.
	 *
	 * @param reference
	 *            clase que encapsula el modelo de parameter antes y después de
	 *            ser modificado.
	 */

	public void updateParameter(ReferencesES<Parameter> reference) {

		updateReferenceByScript(reference, parameterClassInReference, parameterPropertyPath, nestingDepthOfMeasurementProperty, true);
	}

	/**
	 * Función para modificar las referencias de unit en surveyparams en caso de
	 * ser necesario.
	 *
	 * @param reference
	 *            clase que encapsula el modelo de unit antes y después de ser
	 *            modificado.
	 */

	public void updateUnit(ReferencesES<Unit> reference) {

		updateReferenceByScript(reference, unitClassInReference, unitPropertyPath, nestingDepthOfMeasurementProperty, true);
	}

	/**
	 * Función para modificar las referencias de contactRole en surveyparams en
	 * caso de ser necesario.
	 *
	 * @param reference
	 *            clase que encapsula el modelo de contactRole antes y después
	 *            de ser modificado.
	 */

	public void updateContactRole(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, contactRoleClassInReference, contactRolePropertyPath, nestingDepthOfDataDefinitionProperty, true);
	}
}
