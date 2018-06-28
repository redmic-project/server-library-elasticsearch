package es.redmic.es.series.common.repository;

import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;

import es.redmic.es.common.repository.IRBaseESRepository;
import es.redmic.models.es.common.model.BaseAbstractES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.series.common.model.SeriesHitWrapper;
import es.redmic.models.es.series.common.model.SeriesHitsWrapper;
import es.redmic.models.es.series.common.model.SeriesSearchWrapper;

public interface IRSeriesESRepository<TModel extends BaseAbstractES> extends IRBaseESRepository<TModel> {

	public SeriesHitWrapper<?> findById(String id, String parentId, String grandparentId);
	public SeriesSearchWrapper<?> searchByIds(String[] ids);
	public SeriesHitsWrapper<?> mget(MgetDTO dto, String parentId, String grandparentId);
	public SeriesSearchWrapper<?> find(DataQueryDTO queryDTO, String parentId, String grandparentId);
	public SeriesSearchWrapper<?> find(DataQueryDTO queryDTO);
	public List<SeriesSearchWrapper<?>> multiFind(List<SearchRequestBuilder> searchs);
	public SeriesHitWrapper<?> findById(String id);
	public SeriesHitsWrapper<?> mget(MgetDTO dto);
}