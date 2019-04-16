package es.redmic.es.common.objectFactory;

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

import es.redmic.es.data.common.service.RWDataESService;
import es.redmic.models.es.common.dto.DTO;
import es.redmic.models.es.common.model.BaseES;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.ObjectFactory;

@Component
public class ModelESFactory implements ObjectFactory<BaseES<?>> {

	@Override
	public BaseES<?> create(Object source, MappingContext mappingContext) {
		
		if (source == null || !(source instanceof DTO))
			return null;
		
		RWDataESService<?, ?> service = (RWDataESService<?, ?>) mappingContext.getProperty("service");
		
		DTO dto = (DTO) source;
		if (dto != null && dto.getId() != null)
			return (BaseES<?>) service.findById(dto.getId().toString());
		return null;
	}
}
