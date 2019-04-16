package es.redmic.es.geodata.geofixedstation.mapper;

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

import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.geojson.common.dto.FixedSurveySeriesPropertiesDTO;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class FixedSurveySeriesPropertiesESMapper
		extends CustomMapper<GeoDataProperties, FixedSurveySeriesPropertiesDTO> {

	@Override
	public void mapAtoB(GeoDataProperties a, FixedSurveySeriesPropertiesDTO b, MappingContext context) {

		String geoDataPrefix = (String) context.getProperty("geoDataPrefix");

		if (geoDataPrefix == null)
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
	}

	@Override
	public void mapBtoA(FixedSurveySeriesPropertiesDTO b, GeoDataProperties a, MappingContext context) {

		String geoDataPrefix = (String) context.getProperty("geoDataPrefix");

		if (geoDataPrefix == null)
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);

		a.getSite().setId(DataMapperUtils.convertIdentifier(b.getSite().getId(), geoDataPrefix));
	}
}
