package es.redmic.es.maintenance.parameter.service;

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
import es.redmic.es.maintenance.parameter.repository.UnitESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.parameter.dto.UnitDTO;
import es.redmic.models.es.maintenance.parameter.model.Unit;

@Service
public class UnitESService extends MetaDataESService<Unit, UnitDTO> {

	@Autowired
	ParameterESService parameterESService;

	@Autowired
	GeoFixedTimeSeriesESService geoFixedTimeSeriesESService;

	/* Array de campos indexado en la referencia */
	private static Class<DomainES> unitTypeClassInReference = DomainES.class;
	/* Path de elastic para buscar por accessibility */
	private String unitTypePropertyPath = "unitType.id";

	@Autowired
	public UnitESService(UnitESRepository repository) {
		super(repository);
	}

	/**
	 * Función para modificar las referencias de unitType en unit en caso de ser
	 * necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de unitType antes y después de
	 *            ser modificado.
	 */

	public void updateUnitType(ReferencesES<DomainES> reference) {

		updateReference(reference, unitTypeClassInReference, unitTypePropertyPath);
	}

	@Override
	public void postUpdate(ReferencesES<Unit> reference) {

		parameterESService.updateUnit(reference);
		geoFixedTimeSeriesESService.updateUnit(reference);
	}
}
