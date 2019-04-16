package es.redmic.es.series.objectcollecting.mapper;

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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.ConfidenceESService;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.geojson.common.domain.dto.ConfidenceDTO;
import es.redmic.models.es.maintenance.objects.dto.ObjectClassificationDTO;
import es.redmic.models.es.maintenance.objects.dto.ObjectCollectingDTO;
import es.redmic.models.es.maintenance.objects.model.ObjectClassification;
import es.redmic.models.es.maintenance.objects.model.ObjectCollecting;
import es.redmic.models.es.series.objectcollecting.dto.ObjectCollectingSeriesDTO;
import es.redmic.models.es.series.objectcollecting.model.ObjectCollectingSeries;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ObjectCollectingSeriesESMapper extends CustomMapper<ObjectCollectingSeries, ObjectCollectingSeriesDTO> {
	
	@Autowired
	ConfidenceESService confidenceService;
	
	@Override
	public void mapAtoB(ObjectCollectingSeries a, ObjectCollectingSeriesDTO b, MappingContext context) {
		
		b.setObject(mapperFacade.mapAsList(a.getObject(), ObjectClassificationDTO.class));
		if (a.getLocalityConfidence() != null)
			b.setConfidence(mapperFacade.map(a.getLocalityConfidence(), ConfidenceDTO.class));
	}
	
	@Override
	public void mapBtoA(ObjectCollectingSeriesDTO b, ObjectCollectingSeries a, MappingContext context) {
		
		if (b.getConfidence() != null) {
			a.setLocalityConfidence((DomainES) mapperFacade.newObject(b.getConfidence(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(confidenceService)));
		}
		ConfidenceDTO defaultConfidence = new ConfidenceDTO();
		defaultConfidence.setId(4L);
		a.setConfidence((DomainES) mapperFacade.newObject(defaultConfidence, DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(confidenceService)));
		
		List<ObjectClassification> objectGroupList = new ArrayList<ObjectClassification>();
		for (int i=0; i < b.getObject().size(); i++) {
			ObjectClassificationDTO item = b.getObject().get(i);
			
			ObjectClassification objectClassification = new ObjectClassification();/*mapperFacade.map(mapperFacade.newObject(item, DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(objectGroupESService)), ObjectClassification.class);*/
			objectClassification.setId(item.getId());
			List<ObjectCollecting> objectModelList = new ArrayList<ObjectCollecting>(); 
			for (int j=0; j<item.getClassification().size(); j++) {
				ObjectCollectingDTO classificationDTO = item.getClassification().get(j);
				
				objectModelList.add(mapperFacade.map(classificationDTO, ObjectCollecting.class));
			}
			objectClassification.setClassification(objectModelList);
			objectClassification.setName(getClassificationName(objectModelList));
			objectGroupList.add(objectClassification);
		}
		a.setObject(objectGroupList);
	}
	
	private String getClassificationName(List<ObjectCollecting> objectTypeList) {
		
		for (int i=0; i<objectTypeList.size(); i++) {
			if (objectTypeList.get(i).getObjectType().getLevel() == 2)
				return objectTypeList.get(i).getObjectType().getName();
		}
		return null;
	}
}
