package es.redmic.es.series.common.service;

import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.series.common.dto.SeriesCommonDTO;
import es.redmic.models.es.series.common.model.SeriesCommon;

public interface IRSeriesESService<TModel extends SeriesCommon, TDTO extends SeriesCommonDTO> {

	public TDTO get(String id, String parentId, String grandparentId);
	public TModel findById(String id, String parentId, String grandparentId);
	public TDTO searchById(String id);
	public JSONCollectionDTO find(DataQueryDTO query);
	public JSONCollectionDTO find(DataQueryDTO query, String parentId, String grandparentId);
	public JSONCollectionDTO mget(MgetDTO dto, String parentId, String grandparentId);
}
