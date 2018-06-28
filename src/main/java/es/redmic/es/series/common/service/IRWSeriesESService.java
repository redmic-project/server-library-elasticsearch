package es.redmic.es.series.common.service;

import es.redmic.models.es.series.common.model.SeriesCommon;

public interface IRWSeriesESService<TModel extends SeriesCommon> {

	public TModel save(TModel modelToIndex);

	public TModel update(TModel modelToIndex);

	public void delete(String id, String parentId, String grandparentId);
}
