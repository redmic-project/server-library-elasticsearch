package es.redmic.es.common.service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.es.common.repository.SelectionWorkRepository;
import es.redmic.es.common.utils.ElasticPersistenceUtils;
import es.redmic.es.common.utils.ElasticSearchUtils;
import es.redmic.es.data.common.service.RDataESService;
import es.redmic.exception.elasticsearch.ESSelectionWorkException;
import es.redmic.models.es.common.dto.SelectionWorkDTO;
import es.redmic.models.es.common.model.Selection;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

@Service
public class SelectionWorkService implements ISelectionWorkService<SelectionWorkDTO> {

	@Autowired
	ElasticPersistenceUtils<Selection> elasticPersistenceUtils;

	@Autowired
	protected SelectionWorkRepository repository;

	protected String selectScriptPath = "/scripts/select.txt";

	protected String deselectScriptPath = "/scripts/deselect.txt";

	protected String selectAllScriptPath = "/scripts/select-all.txt";

	protected String reverseScriptPath = "/scripts/reverse-selection.txt";

	protected String clearScriptPath = "/scripts/clear-selection.txt";

	public enum Actions {
		select, deselect, selectAll, reverse
	}

	@Autowired
	protected ObjectMapper objectMapper;

	protected final Logger LOGGER = LoggerFactory.getLogger(SelectionWorkService.class);

	public SelectionWorkService() {
	}

	public SelectionWorkDTO update(SelectionWorkDTO dto, RDataESService<?, ?> service) {

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
			} else if (dto.getAction().equals(Actions.selectAll.toString())) {
				result = getAllIds(dto.getQuery(), service, dto.getIdProperty());
				script = ElasticSearchUtils.getScriptFile(selectAllScriptPath);
			} else if (dto.getAction().equals(Actions.reverse.toString())) {
				result = getAllIds(dto.getQuery(), service, dto.getIdProperty());
				script = ElasticSearchUtils.getScriptFile(reverseScriptPath);
			}
			// else clearSelection by default (save [])
			model.setIds(result);
			model.setName(dto.getAction());

			dto = repository.updateSelection(model, script, true);
			dto.setAction(model.getName());
			if (dto.getAction().equals(Actions.deselect.toString()))
				dto.setIds(model.getIds());
		} else {
			LOGGER.debug("No se ha podido almacenar la selección de trabajo ya que no existe el id requerido");
			throw new ESSelectionWorkException();
		}
		return dto;
	}

	public SelectionWorkDTO save(SelectionWorkDTO dto, RDataESService<?, ?> service) {

		Selection model = objectMapper.convertValue(dto, Selection.class);

		model.setDate(new DateTime().toDateTimeISO());

		List<String> result = model.getIds();

		if (dto.getAction().equals(Actions.selectAll.toString()) || dto.getAction().equals(Actions.reverse.toString()))
			result = getAllIds(dto.getQuery(), service, dto.getIdProperty());

		model.setIds(result);
		model.setId(UUID.randomUUID().toString());
		model.setName(dto.getAction());
		repository.save(model);

		dto = objectMapper.convertValue(model, SelectionWorkDTO.class);
		dto.setAction(model.getName());

		return dto;
	}

	public SelectionWorkDTO get(String id) {
		try {
			return objectMapper.convertValue(repository.findById(id).get_source(), SelectionWorkDTO.class);
		} catch (Exception e) {
			LOGGER.debug("No se ha podido obtener la selección de trabajo almacenada");
			throw new ESSelectionWorkException(e);
		}
	}

	public List<String> getAllIds(DataQueryDTO query, RDataESService<?, ?> service, String idProperty) {
		if (query == null)
			query = new DataQueryDTO();
		query.setSize(null);
		return service.getAllIds(query, idProperty);
	}
}
