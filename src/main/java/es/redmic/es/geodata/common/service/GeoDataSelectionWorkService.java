package es.redmic.es.geodata.common.service;

import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.es.common.repository.SelectionWorkRepository;
import es.redmic.es.common.service.SelectionWorkService;
import es.redmic.exception.elasticsearch.ESSelectionWorkException;
import es.redmic.models.es.common.dto.SelectionWorkDTO;
import es.redmic.models.es.common.model.Selection;

@Service
public class GeoDataSelectionWorkService extends SelectionWorkService {

	@Autowired
	SelectionWorkRepository repository;

	private String selectScript = "update-selections-ids";

	private String deselectScript = "rm-selections-ids";

	private String clearScript = "clear-selections-ids";

	public enum Actions {
		select, deselect, selectAll, reverse
	}

	@Autowired
	protected ObjectMapper objectMapper;

	public GeoDataSelectionWorkService() {
	}

	public SelectionWorkDTO update(SelectionWorkDTO dto) {

		Selection model = objectMapper.convertValue(dto, Selection.class);

		model.setDate(new DateTime().toDateTimeISO());

		if (model.getId() != null) {

			List<String> result = null;
			String script = clearScript;
			if (dto.getAction().equals(Actions.select.toString())) {
				result = model.getIds();
				script = selectScript;
			} else if (dto.getAction().equals(Actions.deselect.toString())) {
				result = model.getIds();
				script = deselectScript;
			}
			// else clearSelection by default (save [])
			model.setIds(result);
			model.setName(dto.getAction());

			dto = repository.updateSelection(model, script);
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

	public SelectionWorkDTO get(String id) {
		try {
			return objectMapper.convertValue(repository.findById(id).get_source(), SelectionWorkDTO.class);
		} catch (Exception e) {
			throw new ESSelectionWorkException(e);
		}
	}
}
