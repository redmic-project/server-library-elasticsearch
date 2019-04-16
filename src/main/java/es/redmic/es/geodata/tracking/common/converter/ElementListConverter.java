package es.redmic.es.geodata.tracking.common.converter;

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

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.PlatformESService;
import es.redmic.es.administrative.taxonomy.service.AnimalESService;
import es.redmic.es.common.utils.ElasticSearchUtils;
import es.redmic.models.es.common.dto.UuidDTO;
import es.redmic.models.es.geojson.common.model.Aggregations;
import es.redmic.models.es.geojson.tracking.common.ElementListDTO;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

@Component
public class ElementListConverter extends CustomConverter<Aggregations, ElementListDTO> {

	@Autowired
	AnimalESService animalESService;

	@Autowired
	PlatformESService platformESService;

	@SuppressWarnings("unchecked")
	@Override
	public ElementListDTO convert(Aggregations source, Type<? extends ElementListDTO> destinationType) {

		ElementListDTO elementList = new ElementListDTO();

		Map<String, Object> aggregations = source.getAttributes();
		if (aggregations == null || aggregations.size() == 0)
			return elementList;

		Map<String, Object> animals = (Map<String, Object>) aggregations.get("animal");

		if (!animals.isEmpty()) {
			List<Map<String, Object>> animalIds = ElasticSearchUtils.getBucketsFromAggregations(animals);

			for (int i = 0; i < animalIds.size(); i++) {
				String animalId = animalIds.get(i).get("key").toString();
				elementList.add(mapperFacade.map(animalESService.findById(animalId), UuidDTO.class));
			}
		}

		Map<String, Object> platforms = (Map<String, Object>) aggregations.get("platform");

		if (!platforms.isEmpty()) {
			List<Map<String, Object>> platformIds = ElasticSearchUtils.getBucketsFromAggregations(platforms);

			for (int i = 0; i < platformIds.size(); i++) {
				String platformId = platformIds.get(i).get("key").toString();
				elementList.add(mapperFacade.map(platformESService.findById(platformId), UuidDTO.class));
			}
		}

		return elementList;
	}
}
