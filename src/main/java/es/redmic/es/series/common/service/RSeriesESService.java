package es.redmic.es.series.common.service;

import java.util.HashMap;
import java.util.Map;

import es.redmic.es.common.service.RBaseESService;
import es.redmic.es.series.common.repository.RSeriesESRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.dto.AggregationsDTO;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.series.common.dto.SeriesCommonDTO;
import es.redmic.models.es.series.common.model.SeriesCommon;
import es.redmic.models.es.series.common.model.SeriesHitWrapper;
import es.redmic.models.es.series.common.model.SeriesSearchWrapper;
import ma.glasnost.orika.MappingContext;

public abstract class RSeriesESService<TModel extends SeriesCommon, TDTO extends SeriesCommonDTO>
		extends RBaseESService<TModel, TDTO> implements IRSeriesESService<TModel, TDTO> {

	private RSeriesESRepository<TModel> repository;

	Map<Object, Object> globalProperties = new HashMap<Object, Object>();

	public RSeriesESService() {
		super();
	}

	public RSeriesESService(RSeriesESRepository<TModel> repository) {
		super();
		this.repository = repository;
	}

	public TDTO get(String id, String parentId, String grandparentId) {

		return orikaMapper.getMapperFacade().map(repository.findById(id, parentId, grandparentId), typeOfTDTO,
				getMappingContext());
	}

	@SuppressWarnings("unchecked")
	public TModel findById(String id, String parentId, String grandparentId) {

		SeriesHitWrapper<?> hitWrapper = repository.findById(id, parentId, grandparentId);
		return (TModel) hitWrapper.get_source();
	}

	public TDTO searchById(String id) {

		SeriesSearchWrapper<?> hitsWrapper = repository.searchByIds(new String[] { id });
		if (hitsWrapper.getTotal() == 1)
			return orikaMapper.getMapperFacade().map(hitsWrapper.getSource(0), typeOfTDTO, getMappingContext());
		else if (hitsWrapper.getTotal() > 1) {
			LOGGER.debug("Existe más de un resultado para el mismo id");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
		return null;
	}

	public JSONCollectionDTO find(DataQueryDTO query) {

		return find(query, null, null);
	}

	public JSONCollectionDTO find(DataQueryDTO query, String parentId, String grandparentId) {

		SeriesSearchWrapper<?> result;
		if (parentId == null || grandparentId == null)
			result = repository.find(query);
		else
			result = repository.find(query, parentId, grandparentId);

		JSONCollectionDTO collection = orikaMapper.getMapperFacade().map(result.getHits(), JSONCollectionDTO.class,
				getMappingContext());
		collection.set_aggs(orikaMapper.getMapperFacade().map(result.getAggregations(), AggregationsDTO.class));
		return collection;
	}

	public JSONCollectionDTO mget(MgetDTO dto, String parentId, String grandparentId) {

		return orikaMapper.getMapperFacade().map(repository.mget(dto, parentId, grandparentId), JSONCollectionDTO.class,
				getMappingContext());
	}

	public SimpleQueryDTO createSimpleQueryDTOFromQueryParams(Integer from, Integer size) {
		return repository.createSimpleQueryDTOFromQueryParams(from, size);
	}

	protected MappingContext getMappingContext() {

		globalProperties.put("targetTypeDto", typeOfTDTO);
		return new MappingContext(globalProperties);
	}
}
