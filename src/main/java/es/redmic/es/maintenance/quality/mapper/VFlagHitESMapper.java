package es.redmic.es.maintenance.quality.mapper;

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

import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.maintenance.quality.dto.VFlagDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class VFlagHitESMapper extends CustomMapper<DataHitWrapper, VFlagDTO> {

	@Override
	public void mapAtoB(DataHitWrapper a, VFlagDTO b, MappingContext context) {
		
		mapperFacade.map(a.get_source(), b);	
	}
}
