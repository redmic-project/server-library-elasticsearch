package es.redmic.es.data.common.repository;

import es.redmic.es.common.repository.IRBaseESRepository;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.data.common.model.DataHitsWrapper;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public interface IRDataESRepository<TModel extends BaseES<?>> extends IRBaseESRepository<TModel> {

	public DataHitWrapper<?> findById(String id);
	public DataSearchWrapper<?> searchByIds(String[] ids);
	public DataHitsWrapper<?> mget(MgetDTO dto);
	public DataSearchWrapper<?> find(DataQueryDTO queryDTO);
}