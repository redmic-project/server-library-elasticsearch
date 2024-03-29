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

import es.redmic.es.geodata.geofixedstation.repository.GeoFixedObjectCollectingSeriesESRepository;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.geojson.common.model.GeoLineStringData;
import es.redmic.models.es.geojson.geofixedstation.dto.GeoFixedObjectCollectingSeriesDTO;
import ma.glasnost.orika.MappingContext;

@Service
public class GeoFixedObjectCollectingSeriesESService extends GeoFixedBaseESService<GeoFixedObjectCollectingSeriesDTO, GeoLineStringData> {

	@Autowired
	public GeoFixedObjectCollectingSeriesESService(GeoFixedObjectCollectingSeriesESRepository respository) {
		super(respository);
	}

	@Override
	public GeoLineStringData mapper(GeoFixedObjectCollectingSeriesDTO dtoToIndex) {

		MappingContext context = orikaMapper.getMappingContext();
		context.setProperty("uuid", dtoToIndex.getUuid());
		context.setProperty("geoDataPrefix", DataPrefixType.OBJECT_COLLECTING);

		GeoLineStringData model = orikaMapper.getMapperFacade().map(dtoToIndex, GeoLineStringData.class, context);
		if (dtoToIndex.getProperties() != null)
			model.getProperties().setActivityId(dtoToIndex.getProperties().getActivityId());
		return model;
	}
}
