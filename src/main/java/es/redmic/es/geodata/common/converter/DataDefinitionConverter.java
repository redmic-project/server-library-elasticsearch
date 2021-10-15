package es.redmic.es.geodata.common.converter;

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

import es.redmic.models.es.maintenance.parameter.dto.DataDefinitionDTO;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

@Component
public class DataDefinitionConverter extends BidirectionalConverter<Long, DataDefinitionDTO> {

	@Override
	public DataDefinitionDTO convertTo(Long source, Type<DataDefinitionDTO> destinationType, MappingContext mappingContext) {
		DataDefinitionDTO dto = new DataDefinitionDTO();
		dto.setId(source);
		return dto;
	}

	@Override
	public Long convertFrom(DataDefinitionDTO source, Type<Long> destinationType, MappingContext mappingContext) {
		return source.getId();
	}
}
