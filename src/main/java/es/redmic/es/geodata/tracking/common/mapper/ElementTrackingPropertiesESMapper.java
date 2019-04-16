package es.redmic.es.geodata.tracking.common.mapper;

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

import es.redmic.models.es.administrative.dto.PlatformCompactDTO;
import es.redmic.models.es.administrative.taxonomy.dto.AnimalCompactDTO;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.tracking.common.ElementTrackingPropertiesDTO;
import ma.glasnost.orika.MappingContext;

@Component
public class ElementTrackingPropertiesESMapper extends TrackingPropertiesESMapper<GeoDataProperties, ElementTrackingPropertiesDTO> {

	@Override
	public void mapAtoB(GeoDataProperties a, ElementTrackingPropertiesDTO b, MappingContext context) {

		super.mapAtoB(a, b, context);

		if (a.getInTrack().getPlatform() != null)
			b.setElement(mapperFacade.map(a.getInTrack().getPlatform(), PlatformCompactDTO.class));
		else if (a.getCollect() != null && a.getCollect().getAnimal() != null) {
			b.setElement(mapperFacade.map(a.getCollect().getAnimal(), AnimalCompactDTO.class));
			((AnimalCompactDTO) b.getElement()).setTaxon(mapperFacade.map(a.getCollect().getTaxon(), TaxonDTO.class));
		}
	}
}
