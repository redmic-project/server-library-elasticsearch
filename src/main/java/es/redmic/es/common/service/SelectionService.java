package es.redmic.es.common.service;

import java.util.UUID;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.es.common.repository.SelectionRepository;
import es.redmic.es.common.repository.SelectionWorkRepository;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.exception.elasticsearch.ESUpdateException;
import es.redmic.models.es.common.dto.SelectionDTO;
import es.redmic.models.es.common.model.Selection;
import es.redmic.models.es.data.common.model.DataHitWrapper;

@Service
public class SelectionService extends SettingsServices<Selection, SelectionDTO> {

	@Autowired
	SelectionWorkRepository selectionWorkRepository;

	SelectionRepository repository;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	public SelectionService(SelectionRepository repository) {
		super(repository);
		this.repository = repository;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Selection save(SelectionDTO dto) {

		String userId = userService.getUserId();

		Selection model = objectMapper.convertValue(dto, Selection.class);
		if (dto.getIds() != null && dto.getIds().size() == 1) {
			String selectionId = dto.getIds().get(0);
			DataHitWrapper<Selection> selectionCurrent = (DataHitWrapper<Selection>) selectionWorkRepository
					.findById(selectionId);
			model.setIds(selectionCurrent.get_source().getIds());
		} else {
			LOGGER.debug("Error al guardar la selección, no encontrada selección de trabajo para hacerla persistente");
			throw new ESUpdateException(new ItemNotFoundException("selectionId", "null"));
		}

		model.setUserId(userId);
		model.setDate(new DateTime().toDateTimeISO());

		if (model.getId() != null) {
			return repository.update(model);
		} else {
			model.setId(UUID.randomUUID().toString());
			return repository.save(model);
		}
	}

	@Override
	public Selection update(SelectionDTO dto) {
		// TODO separae porque ahora entra siempre por save
		return null;
	}
}
