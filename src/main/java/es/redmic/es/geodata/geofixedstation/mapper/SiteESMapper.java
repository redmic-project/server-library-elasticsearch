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

import es.redmic.models.es.geojson.properties.model.Site;
import es.redmic.models.es.maintenance.survey.dto.FixedSurveyDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class SiteESMapper extends CustomMapper<Site, FixedSurveyDTO> {
	
	@Override
	public void mapAtoB(Site a, FixedSurveyDTO b, MappingContext context) {
		
		b.setLeaves(1);
	}
	
	@Override
	public void mapBtoA(FixedSurveyDTO b, Site a, MappingContext context) {		
		
		a.setPath("r."+context.getProperty("uuid"));
	}
}
