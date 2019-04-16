package es.redmic.es.geodata.common.service;

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

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.redmic.es.common.service.RBaseESService;
import es.redmic.es.geodata.common.repository.RGeoDataESRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.dto.AggregationsDTO;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.geojson.common.dto.CategoryListDTO;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.models.es.geojson.common.dto.MetaFeatureDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;
import ma.glasnost.orika.MappingContext;

public abstract class RGeoDataESService<TDTO extends MetaFeatureDTO<?, ?>, TModel extends Feature<?, ?>>
		extends RBaseESService<TModel, TDTO> implements IRGeoDataESService<TDTO, TModel> {

	private RGeoDataESRepository<TModel> repository;

	Map<Object, Object> globalProperties = new HashMap<Object, Object>();

	public RGeoDataESService() {
		super();
	}

	@SuppressWarnings("unchecked")
	public RGeoDataESService(RGeoDataESRepository<TModel> repository) {
		super();
		this.repository = repository;
		this.typeOfTDTO = (Class<TDTO>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

	public TDTO get(String id, String parentId) {

		return orikaMapper.getMapperFacade().map(repository.findById(id, parentId), typeOfTDTO, getMappingContext());
	}

	@SuppressWarnings("unchecked")
	public TModel findById(String id, String parentId) {

		GeoHitWrapper<?, ?> hitWrapper = repository.findById(id, parentId);
		return (TModel) hitWrapper.get_source();
	}

	public TDTO searchById(String id) {

		GeoSearchWrapper<?, ?> hitsWrapper = repository.searchByIds(new String[] { id });
		if (hitsWrapper.getTotal() == 1) {
			TDTO item = orikaMapper.getMapperFacade().map(hitsWrapper.getSource(0), typeOfTDTO, getMappingContext());
			item.getProperties().setActivityId(hitsWrapper.getHits().getHits().get(0).get_parent());
			return item;
		} else if (hitsWrapper.getTotal() > 1) {
			LOGGER.debug("Existe más de un resultado para el mismo id");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
		return null;
	}

	public GeoJSONFeatureCollectionDTO find(DataQueryDTO query) {

		return find(query, null);
	}

	public GeoJSONFeatureCollectionDTO find(DataQueryDTO query, String parentId) {

		GeoSearchWrapper<?, ?> result;
		if (parentId == null)
			result = repository.find(query);
		else
			result = repository.find(query, parentId);

		if (result.getTotal() == 0)
			return new GeoJSONFeatureCollectionDTO();

		GeoJSONFeatureCollectionDTO featureCollection = orikaMapper.getMapperFacade().map(result.getHits(),
				GeoJSONFeatureCollectionDTO.class, getMappingContext());
		if (result.getAggregations() != null)
			featureCollection
					.set_aggs(orikaMapper.getMapperFacade().map(result.getAggregations(), AggregationsDTO.class));
		return featureCollection;
	}

	public List<String> suggest(DataQueryDTO queryDTO) {

		return repository.suggest(queryDTO);
	}

	public List<String> suggest(String parentId, DataQueryDTO queryDTO) {

		return repository.suggest(parentId, queryDTO);
	}

	public GeoJSONFeatureCollectionDTO mget(MgetDTO dto, String parentId) {

		return orikaMapper.getMapperFacade().map(repository.mget(dto, parentId), GeoJSONFeatureCollectionDTO.class,
				getMappingContext());
	}

	public SimpleQueryDTO createSimpleQueryDTOFromTextQueryParams(Integer from, Integer size) {
		return repository.createSimpleQueryDTOFromTextQueryParams(from, size);
	}

	public SimpleQueryDTO createSimpleQueryDTOFromSuggestQueryParams(String[] fields, String text, Integer size) {
		return repository.createSimpleQueryDTOFromSuggestQueryParams(fields, text, size);
	}

	public CategoryListDTO getCategories(String parentId, DataQueryDTO queryDTO) {
		return repository.getCategories(parentId, queryDTO);
	}

	protected MappingContext getMappingContext() {

		globalProperties.put("targetTypeDto", typeOfTDTO);
		globalProperties.put("geoDataPrefix", DataPrefixType.getPrefixTypeFromClass(typeOfTDTO));
		return new MappingContext(globalProperties);
	}
}
