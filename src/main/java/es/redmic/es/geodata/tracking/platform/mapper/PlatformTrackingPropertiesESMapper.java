package es.redmic.es.geodata.tracking.platform.mapper;

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
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.PlatformESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.geodata.tracking.common.mapper.TrackingPropertiesESMapper;
import es.redmic.models.es.administrative.dto.PlatformCompactDTO;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.administrative.model.PlatformCompact;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.tracking.platform.dto.PlatformTrackingPropertiesDTO;
import ma.glasnost.orika.MappingContext;

@Component
public class PlatformTrackingPropertiesESMapper extends TrackingPropertiesESMapper<GeoDataProperties, PlatformTrackingPropertiesDTO> {

	@Autowired
	PlatformESService platformESService;

	@Override
	public void mapAtoB(GeoDataProperties a, PlatformTrackingPropertiesDTO b, MappingContext context) {

		super.mapAtoB(a, b, context);
		
		b.setPlatform(mapperFacade.map(a.getInTrack().getPlatform(), PlatformCompactDTO.class));
	}

	@Override
	public void mapBtoA(PlatformTrackingPropertiesDTO b, GeoDataProperties a, MappingContext context) {

		super.mapBtoA(b, a, context);

		Platform platform = (Platform) mapperFacade.newObject(b.getPlatform(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(platformESService));
		
		a.getInTrack().setPlatform(mapperFacade.map(platform, PlatformCompact.class));
	}
}
