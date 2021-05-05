package es.redmic.es.maintenance.objects.service;

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

import es.redmic.es.common.service.HierarchicalESService;
import es.redmic.es.maintenance.objects.repository.ObjectTypeESRepository;
import es.redmic.es.series.objectcollecting.service.ObjectCollectingSeriesESService;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.objects.dto.ObjectClassificationDTO;
import es.redmic.models.es.maintenance.objects.dto.ObjectTypeDTO;
import es.redmic.models.es.maintenance.objects.model.ObjectType;

@Service
public class ObjectTypeESService extends HierarchicalESService<ObjectType, ObjectTypeDTO> {

	@Autowired
	ObjectCollectingSeriesESService objectCollectingSeriesESService;

	@Autowired
	public ObjectTypeESService(ObjectTypeESRepository repository) {
		super(repository);
	}

	public ObjectClassificationDTO getObjectClassification(String objectTypeId) {

		return orikaMapper.getMapperFacade().convert(findById(objectTypeId), ObjectClassificationDTO.class, null, null);
	}

	@Override
	protected void postUpdate(ReferencesES<ObjectType> reference) {

		super.postUpdate(reference);

		objectCollectingSeriesESService.updateObjectType(reference);
	}
}
