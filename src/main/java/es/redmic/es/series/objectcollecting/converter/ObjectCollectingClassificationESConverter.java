package es.redmic.es.series.objectcollecting.converter;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.maintenance.objects.repository.ObjectTypeESRepository;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.maintenance.objects.dto.ObjectClassificationDTO;
import es.redmic.models.es.maintenance.objects.dto.ObjectCollectingDTO;
import es.redmic.models.es.maintenance.objects.dto.ObjectTypeBaseDTO;
import es.redmic.models.es.maintenance.objects.model.ObjectType;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

@Component
public class ObjectCollectingClassificationESConverter
		extends BidirectionalConverter<ObjectType, ObjectClassificationDTO> {

	@Autowired
	ObjectTypeESRepository repository;

	@SuppressWarnings("unchecked")
	@Override
	public ObjectClassificationDTO convertTo(ObjectType source, Type<ObjectClassificationDTO> destinationType,
		MappingContext mappingContext) {

		ObjectClassificationDTO result = new ObjectClassificationDTO();// mapperFacade.map(source.getObjectGroup(),
																		// ObjectClassificationDTO.class);

		List<ObjectCollectingDTO> classification = new ArrayList<ObjectCollectingDTO>();

		// Guardamos el más específico
		classification.add(getObjectDTO(source));
		// Guardamos los ancestors
		String[] pathSplitted = source.getPath().split("\\."),
				ancestorsIds = Arrays.copyOfRange(pathSplitted, 1, pathSplitted.length - 1);

		for (int i = 0; i < ancestorsIds.length; i++) {
			DataHitWrapper<ObjectType> item = (DataHitWrapper<ObjectType>) repository.findById(ancestorsIds[i]);
			classification.add(getObjectDTO(item.get_source()));
		}
		result.setClassification(classification);

		return result;
	}

	@Override
	public ObjectType convertFrom(ObjectClassificationDTO source, Type<ObjectType> destinationType,
		MappingContext mappingContext) {

		return null;
	}

	private ObjectCollectingDTO getObjectDTO(ObjectType obj) {

		ObjectCollectingDTO objectCollectingDTO = new ObjectCollectingDTO();
		ObjectTypeBaseDTO item = mapperFacade.map(obj, ObjectTypeBaseDTO.class);
		objectCollectingDTO.setObjectType(item);

		return objectCollectingDTO;
	}
}
