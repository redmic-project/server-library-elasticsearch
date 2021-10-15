package es.redmic.es.geodata.isolines.mapper;

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

import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.geojson.common.model.GeoMultiLineStringData;
import es.redmic.models.es.geojson.isolines.dto.IsolinesDTO;
import es.redmic.models.es.series.timeseries.model.TimeSeries;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class IsolinesESMapper extends CustomMapper<GeoMultiLineStringData, IsolinesDTO>{

	@Override
	public void mapBtoA(IsolinesDTO b, GeoMultiLineStringData a, MappingContext context) {

		if (a.getProperties() != null) {
			a.getProperties().getSamplingPlace().setId(DataMapperUtils.convertIdentifier(b.getId(), DataPrefixType.ISOLINES));
			a.getProperties().setActivityId(b.getProperties().getActivityId());

			// Se mapea para poder obtenerlo en el servicio, pero no se indexa en este repositorio

			TimeSeries series = mapperFacade.map(b.getProperties(), TimeSeries.class);

			series.setId(b.getId());
			series.set_parentId(b.getUuid());
			series.set_grandparentId(b.getProperties().getActivityId());

			series.setZ(b.getProperties().getZ() != null ? b.getProperties().getZ() : b.getProperties().getDataDefinition().getZ());
			series.setDeviation(b.getProperties().getDeviation() != null ? b.getProperties().getDeviation() : b.getProperties().getDataDefinition().getZ());

			a.getProperties().setSeries(Arrays.asList(series));
		}
	}
}
