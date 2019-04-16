package es.redmic.es.series.common.utils;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.redmic.es.config.OrikaScanBeanESItfc;
import es.redmic.es.geodata.geofixedstation.service.GeoFixedBaseESService;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.utils.HierarchicalUtils;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.maintenance.parameter.model.DataDefinition;
import es.redmic.models.es.series.common.dto.SeriesBaseDTO;
import es.redmic.models.es.series.timeseries.dto.RawDataItemDTO;

public abstract class DataDefinitionUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataDefinitionUtils.class);

	private static double TOLERANCE_LEVEL = 0.4;

	public static RawDataItemDTO getItemData(OrikaScanBeanESItfc orikaMapper, SeriesBaseDTO item,
			DateTime currentDateTime, Long currentDataDefinitionId, Map<Long, DataDefinition> dataDefinitions) {

		if (item == null)
			return null;

		DateTime nextDateTime = item.getDate();

		DataDefinition dataDefinition = dataDefinitions.get(item.getDataDefinition());
		if (item.getDataDefinition() != currentDataDefinitionId) {

			currentDataDefinitionId = item.getDataDefinition();
			dataDefinition = dataDefinitions.get(currentDataDefinitionId);
		}
		Long timeInterval = dataDefinition.getTimeInterval();

		// TODO: crear excepción propia
		if (dataDefinition.getIsRegularity() && timeInterval == null) {
			LOGGER.debug("Serie temporal con timeInterval no definido");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
		if (dataDefinition.getIsRegularity() && !fulfillIntervalCheck(currentDateTime, nextDateTime, timeInterval)) {
			RawDataItemDTO nullItem = new RawDataItemDTO();
			nullItem.setValue(null);
			nullItem.setDate(currentDateTime.plusSeconds(millisec2sec(timeInterval)).toString());
			return nullItem;
		}
		return orikaMapper.getMapperFacade().map(item, RawDataItemDTO.class);
	}

	/**
	 * Función para obtener el datadefinition almacenado.
	 * 
	 * @param dataDefinitionId
	 *            dataDefinition a buscar
	 * 
	 * @return dataDefinition.
	 */

	public static HashMap<Long, DataDefinition> getDataDefinitions(OrikaScanBeanESItfc orikaMapper,
			List<Integer> dataDefinitionIds, GeoFixedBaseESService<?, ?> geoFixedService) {

		HashMap<Long, DataDefinition> dataDefinitions = new HashMap<Long, DataDefinition>();

		DataDefinition previousDataDefinition = null;

		for (int i = 0; i < dataDefinitionIds.size(); i++) {

			Long dataDefinitionId = ((Number) dataDefinitionIds.get(i)).longValue();
			GeoSearchWrapper<GeoDataProperties, ?> result = geoFixedService.findByDataDefinition(dataDefinitionId);
			if (result == null || result.getTotal() == 0)
				return null;
			DataDefinition dataDefinition = orikaMapper.getMapperFacade().map(
					result.getSource(0).getProperties().getMeasurements().get(0).getDataDefinition(),
					DataDefinition.class);
			dataDefinitions.put(dataDefinitionId, dataDefinition);

			if (previousDataDefinition != null)
				checkDataDefinionIntegrity(previousDataDefinition, dataDefinition);

			previousDataDefinition = dataDefinition;
		}
		return dataDefinitions;
	}

	/**
	 * Función que checkea el path de los datasdefinition enviados en la
	 * consulta sean iguales
	 * 
	 * @return true si los dataDefinitions corresponden al mismo parámetro, en
	 *         la misma estación y con la misma z
	 * 
	 */
	public static void checkDataDefinionIntegrity(DataDefinition previousDataDefinition,
			DataDefinition currentDataDefinition) {

		if (!(HierarchicalUtils.getParentPath(previousDataDefinition.getPath())
				.equals(HierarchicalUtils.getParentPath(currentDataDefinition.getPath())))
				|| (previousDataDefinition.getZ().compareTo(currentDataDefinition.getZ()) != 0)) {
			// TODO: crear excepción propia
			LOGGER.debug(
					"DataDefinitions en la misma timeseries que no corresponden al mismo parámetro y/o estación y/o profundidad");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
	}

	/**
	 * Función para comprobar que el intervalo de tiempo entre los datos
	 * cargados corresponde con el definido en dataDefinition + una tolerancia.
	 * Si el dato viene antes se acepta como válido
	 * 
	 * @param previousDatetime
	 *            tiempo en el dato actual.
	 * @param currentDatetime
	 *            tiempo en el dato siguiente.
	 * @param itemInterval
	 *            intervalo de tiempo definido en segundos.
	 * 
	 * @return true si cumple las restricciones, false en caso contrario.
	 */
	public static Boolean fulfillIntervalCheck(DateTime currentDatetime, DateTime nextDatetime, Long itemInterval) {
		// TODO: crear excepción propia
		if (itemInterval < TOLERANCE_LEVEL) {
			LOGGER.debug("El timeInterval de los datos no es correcto. TimeInterval = " + itemInterval);
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
		int tolerance = new Double(millisec2sec(itemInterval) * TOLERANCE_LEVEL).intValue();
		DateTime calculatedInterval = currentDatetime.plusSeconds(millisec2sec(itemInterval));
		// next <= calculated + tolerance
		return nextDatetime.compareTo(calculatedInterval.plusSeconds(tolerance)) <= 0;
	}

	public static int millisec2sec(Long millisec) {
		return (int) (millisec / 1000);
	}

	@SuppressWarnings("serial")
	public static void addDataDefinitionFieldToReturn(DataQueryDTO query) {

		List<String> returnFields = query.getReturnFields();

		if (returnFields == null)
			returnFields = new ArrayList<String>() {
				{
					add("date");
					add("value");
					add("dataDefinition");
				}
			};
		else
			returnFields.add("dataDefinition");

		query.setReturnFields(returnFields);
	}
}
