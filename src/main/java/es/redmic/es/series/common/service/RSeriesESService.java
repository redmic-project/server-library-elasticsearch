package es.redmic.es.series.common.service;

/*-
 * #%L
 * ElasticSearch
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import es.redmic.es.common.service.RBaseESService;
import es.redmic.es.series.common.repository.RSeriesESRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.dto.AggregationsDTO;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;
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

	protected RSeriesESService() {
		super();
	}

	protected RSeriesESService(RSeriesESRepository<TModel> repository) {
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

	public JSONCollectionDTO find(GeoDataQueryDTO query) {

		return find(query, null, null);
	}

	public JSONCollectionDTO find(GeoDataQueryDTO query, String parentId, String grandparentId) {

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

	@Override
	protected MappingContext getMappingContext() {

		MappingContext context = orikaMapper.getMappingContext();
		context.setProperty("targetTypeDto", typeOfTDTO);

		return context;
	}
}
