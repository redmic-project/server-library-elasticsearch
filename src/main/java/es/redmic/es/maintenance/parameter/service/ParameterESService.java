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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.geodata.geofixedstation.service.GeoFixedTimeSeriesESService;
import es.redmic.es.maintenance.parameter.repository.ParameterESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.models.es.maintenance.parameter.dto.ParameterDTO;
import es.redmic.models.es.maintenance.parameter.model.Parameter;
import es.redmic.models.es.maintenance.parameter.model.ParameterUnit;
import es.redmic.models.es.maintenance.parameter.model.Unit;

@Service
public class ParameterESService extends MetaDataESService<Parameter, ParameterDTO> {

	@Autowired
	GeoFixedTimeSeriesESService geoFixedTimeSeriesESService;
	
	private ParameterESRepository repository;

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> parameterTypeClassInReference = DomainES.class;
	/* Path de elastic para buscar por parameterType */
	private String parameterTypePropertyPath = "parameterType.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<Unit> unitClassInReference = Unit.class;
	/* Path de elastic para buscar por accessibility */
	private String unitPropertyPath = "units.id";

	@Autowired
	public ParameterESService(ParameterESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	/**
	 * Función para modificar las referencias de parameterType en parameter en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de parameterType antes y después
	 *            de ser modificado.
	 */

	public void updateParameterType(ReferencesES<DomainES> reference) {

		updateReference(reference, parameterTypeClassInReference, parameterTypePropertyPath);
	}

	/**
	 * Función para modificar las referencias de unit en parameter en caso de
	 * ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de unitType antes y después de
	 *            ser modificado.
	 */

	public void updateUnit(ReferencesES<Unit> reference) {

		updateReferenceByScript(reference, unitClassInReference, unitPropertyPath);
	}

	@Override
	public void postUpdate(ReferencesES<Parameter> reference) {

		geoFixedTimeSeriesESService.updateParameter(reference);
	}
	
	public List<Unit> getUnits(String parameterId) {

		DataSearchWrapper<Parameter> result = repository.findUnits(parameterId);
		List<Parameter> source = result.getSourceList();
		if (source == null)
			return new ArrayList<Unit>();

		List<Unit> units = new ArrayList<Unit>();
		
		for (int i=0; i < source.size(); i++) {
			List<ParameterUnit> parameterUnits = source.get(i).getUnits();
			for (int j=0; j<parameterUnits.size(); j++)
				units.add(parameterUnits.get(j).getUnit());
		}	
		return units;
	}
}
