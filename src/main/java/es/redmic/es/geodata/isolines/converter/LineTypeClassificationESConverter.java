package es.redmic.es.geodata.isolines.converter;

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

import es.redmic.es.maintenance.line.service.LineTypeESService;
import es.redmic.models.es.maintenance.common.model.ClassificationBase;
import es.redmic.models.es.maintenance.common.model.ClassificationItem;
import es.redmic.models.es.maintenance.line.dto.LineTypeBaseDTO;
import es.redmic.models.es.maintenance.line.model.LineType;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

@Component
public class LineTypeClassificationESConverter
		extends BidirectionalConverter<LineTypeBaseDTO, List<ClassificationItem>> {

	@Autowired
	LineTypeESService service;

	@Override
	public List<ClassificationItem> convertTo(LineTypeBaseDTO source, Type<List<ClassificationItem>> destinationType) {

		List<ClassificationItem> result = new ArrayList<ClassificationItem>();

		ClassificationItem type = getClassificationItem(source.getId().toString());
		// Guardamos el más específico
		result.add(type);
		// Guardamos los ancestors
		String[] pathSplitted = type.getType().getPath().split("\\."),
				ancestorsIds = Arrays.copyOfRange(pathSplitted, 1, pathSplitted.length - 1);

		for (int i = 0; i < ancestorsIds.length; i++) {
			result.add(getClassificationItem(ancestorsIds[i]));
		}
		return result;
	}

	@Override
	public LineTypeBaseDTO convertFrom(List<ClassificationItem> source, Type<LineTypeBaseDTO> destinationType) {

		if (source == null || source.size() == 0)
			return null;

		ClassificationBase specificItem = source.get(0).getType();

		for (int i = 1; i < source.size(); i++) {
			if (source.get(i).getType().getLevel() > specificItem.getLevel())
				specificItem = source.get(i).getType();
		}
		return mapperFacade.map(specificItem, LineTypeBaseDTO.class);
	}

	private ClassificationItem getClassificationItem(String id) {

		ClassificationItem classificationItem = new ClassificationItem();
		LineType item = service.findById(id);
		classificationItem.setType(mapperFacade.map(item, ClassificationBase.class));
		return classificationItem;
	}
}
