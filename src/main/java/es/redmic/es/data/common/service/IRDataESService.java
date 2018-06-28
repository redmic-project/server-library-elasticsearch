package es.redmic.es.data.common.service;

import java.util.List;

import es.redmic.models.es.common.dto.BaseDTO;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;

public interface IRDataESService<TModel extends BaseES<?>, TDTO extends BaseDTO<?>> {

	public TDTO get(String id);
	public TModel findById(String id);
	public TDTO searchById(String id);
	public JSONCollectionDTO find(DataQueryDTO query);
	public List<String> suggest(DataQueryDTO queryDTO);
	public JSONCollectionDTO mget(MgetDTO dto);
}
