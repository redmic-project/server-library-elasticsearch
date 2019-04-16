package es.redmic.es.geodata.tracking.animal.mapper;

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
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.tracking.animal.dto.AnimalTrackingDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class AnimalTrackingESMapper extends CustomMapper<GeoPointData, AnimalTrackingDTO> {

	@Override
	public void mapBtoA(AnimalTrackingDTO b, GeoPointData a, MappingContext context) {

		if ( a.getProperties() != null) {
			
			String id = DataMapperUtils.convertIdentifier(b.getId(), DataPrefixType.ANIMAL_TRACKING);
			a.getProperties().getCollect().setId(id);
			a.getProperties().getInTrack().setId(id);
			a.set_parentId(b.getProperties().getActivityId());
		}
	}
}
