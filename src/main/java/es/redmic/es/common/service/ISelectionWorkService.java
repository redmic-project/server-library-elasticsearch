package es.redmic.es.common.service;

import java.util.List;

import es.redmic.es.data.common.service.RDataESService;
import es.redmic.models.es.common.dto.SelectionBaseDTO;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

public interface ISelectionWorkService<TDTO extends SelectionBaseDTO> {

	public TDTO get(String id);

	public TDTO save(TDTO dto, RDataESService<?, ?> service);

	public TDTO update(TDTO dto, RDataESService<?, ?> service);

	public List<String> getAllIds(DataQueryDTO query, RDataESService<?, ?> service, String idProperty);
}
