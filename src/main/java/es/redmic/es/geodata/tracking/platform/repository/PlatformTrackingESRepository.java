package es.redmic.es.geodata.tracking.platform.repository;

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

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.queryFactory.geodata.PlatformTrackingQueryUtils;
import es.redmic.es.geodata.tracking.common.repository.ClusterTrackingESRepository;
import es.redmic.models.es.common.request.dto.CategoryPathInfo;
import es.redmic.models.es.geojson.common.model.GeoPointData;

@Repository
public class PlatformTrackingESRepository extends ClusterTrackingESRepository<GeoPointData> {
	
	@Value("${controller.mapping.DEVICE}")
	private String DEVICE_TARGET;
	
	@Value("${controller.mapping.PLATFORM}")
	private String PLATFORM_TARGET;

	public PlatformTrackingESRepository() {
		super();
		setInternalQuery(PlatformTrackingQueryUtils.INTERNAL_QUERY);
	}

	@Override
	public HashMap<String, CategoryPathInfo> getCategoriesPaths() {
		
		// TODO: usar el diccionario de dto a model cuando esté implementado
		HashMap<String, CategoryPathInfo> categoriesPaths = new HashMap<>();
		
		categoriesPaths.put("properties.device.id", new CategoryPathInfo("properties.inTrack.device.id", DEVICE_TARGET));
		categoriesPaths.put("properties.platform.id", new CategoryPathInfo("properties.inTrack.platform.id", PLATFORM_TARGET));

		return categoriesPaths;
	}
}
