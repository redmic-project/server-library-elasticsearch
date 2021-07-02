package es.redmic.es.geodata.common.service;

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
import java.util.UUID;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import es.redmic.es.common.service.SelectionWorkService;
import es.redmic.exception.elasticsearch.ESSelectionWorkException;
import es.redmic.models.es.common.dto.SelectionWorkDTO;
import es.redmic.models.es.common.model.Selection;
import es.redmic.es.common.utils.ElasticSearchUtils;

@Service
public class GeoDataSelectionWorkService extends SelectionWorkService {

	public GeoDataSelectionWorkService() {
	}

	public SelectionWorkDTO update(SelectionWorkDTO dto) {

		Selection model = objectMapper.convertValue(dto, Selection.class);

		model.setDate(new DateTime().toDateTimeISO());

		if (model.getId() != null) {

			List<String> result = null;
			String script = ElasticSearchUtils.getScriptFile(clearScriptPath);
			if (dto.getAction().equals(Actions.select.toString())) {
				result = model.getIds();
				script = ElasticSearchUtils.getScriptFile(selectScriptPath);
			} else if (dto.getAction().equals(Actions.deselect.toString())) {
				result = model.getIds();
				script = ElasticSearchUtils.getScriptFile(deselectScriptPath);
			}
			// else clearSelection by default (save [])
			model.setIds(result);
			model.setName(dto.getAction());

			dto = repository.updateSelection(model, script, true);
			dto.setAction(model.getName());
			if (dto.getAction().equals(Actions.deselect.toString()))
				dto.setIds(model.getIds());
		} else
			throw new ESSelectionWorkException();

		return dto;
	}

	public SelectionWorkDTO save(SelectionWorkDTO dto) {

		Selection model = objectMapper.convertValue(dto, Selection.class);

		model.setDate(new DateTime().toDateTimeISO());

		List<String> result = model.getIds();

		model.setIds(result);
		model.setId(UUID.randomUUID().toString());
		model.setName(dto.getAction());
		repository.save(model);

		dto = objectMapper.convertValue(model, SelectionWorkDTO.class);
		dto.setAction(model.getName());

		return dto;
	}

	@Override
	public SelectionWorkDTO get(String id) {
		try {
			return objectMapper.convertValue(repository.findById(id).get_source(), SelectionWorkDTO.class);
		} catch (Exception e) {
			throw new ESSelectionWorkException(e);
		}
	}
}
