package es.redmic.es.common.service;

import java.util.List;

import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.dto.SettingsDTO;
import es.redmic.models.es.common.model.Settings;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

public interface ISettingsService<TModel extends Settings, TDTO extends SettingsDTO> {

	public TDTO get(String id);

	public JSONCollectionDTO findAll(DataQueryDTO dto, String service);

	public TModel save(TDTO dto);

	public TModel update(TDTO dto);

	public void delete(String id);

	public List<String> suggest(DataQueryDTO queryDTO);
}
