package es.redmic.es.geodata.common.repository;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JavaType;

import es.redmic.es.common.queryFactory.geodata.GeoDataQueryUtils;
import es.redmic.es.common.repository.RBaseESRepository;
import es.redmic.es.common.service.UserUtilsServiceItfc;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.models.es.common.query.dto.AggsPropertiesDTO;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.common.request.dto.CategoryPathInfo;
import es.redmic.models.es.geojson.common.dto.CategoryListDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.models.es.geojson.common.model.GeoHitsWrapper;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;

public abstract class RGeoDataESRepository<TModel extends Feature<?, ?>> extends RBaseESRepository<TModel>
		implements IRGeoDataESRepository<TModel> {

	@Autowired
	UserUtilsServiceItfc userService;

	protected RGeoDataESRepository(String[] index, String type) {
		super(index, type);
	}

	@Override
	public GeoHitWrapper<?, ?> findById(String id) {

		return findById(id, null);
	}

	/*
	 * Sobrescribe método base para añadir query de control de accesso a datos
	 */
	@Override
	public GeoHitWrapper<?, ?> findById(String id, String parentId) {

		BoolQueryBuilder query = GeoDataQueryUtils.getItemsQuery(id, parentId, userService.getAccessibilityControl());

		GeoSearchWrapper<?, ?> result = findBy(query);

		if (result.getHits() == null || result.getHits().getHits() == null || result.getHits().getHits().size() != 1)
			throw new ItemNotFoundException("id", id);

		return result.getHits().getHits().get(0);
	}

	@Override
	public GeoHitsWrapper<?, ?> mget(MgetDTO dto) {
		return mget(dto, null);
	}

	/*
	 * Sobrescribe método base para añadir query de control de accesso a datos
	 */
	@Override
	public GeoHitsWrapper<?, ?> mget(MgetDTO dto, String parentId) {

		List<String> ids = dto.getIds();

		BoolQueryBuilder query = GeoDataQueryUtils.getItemsQuery(ids, parentId, userService.getAccessibilityControl());

		GeoSearchWrapper<?, ?> result = findBy(query, dto.getFields());

		if (result.getHits() == null || result.getHits().getHits() == null)
			throw new ItemNotFoundException("ids", dto.getIds().toString());

		if (result.getHits().getHits().size() != ids.size()) {

			for (GeoHitWrapper<?, ?> hit : result.getHits().getHits()) {
				ids.remove(hit.get_id());
			}

			throw new ItemNotFoundException("ids", ids.toString());
		}

		return result.getHits();
	}

	@Override
	public GeoSearchWrapper<?, ?> searchByIds(String[] ids) {

		return findBy(QueryBuilders.idsQuery().addIds(ids));
	}

	protected GeoSearchWrapper<?, ?> findBy(QueryBuilder queryBuilder) {

		return findBy(queryBuilder, null);
	}

	protected GeoSearchWrapper<?, ?> findBy(QueryBuilder queryBuilder, List<String> returnFields) {

		return searchResponseToWrapper(searchRequest(queryBuilder, returnFields),
				getSourceType(GeoSearchWrapper.class));
	}

	public List<String> suggest(String parentId, GeoDataQueryDTO queryDTO) {

		QueryBuilder serviceQuery = GeoDataQueryUtils.getHierarchicalQuery(queryDTO, parentId);

		return suggest(queryDTO, serviceQuery);
	}

	@Override
	public GeoSearchWrapper<?, ?> find(GeoDataQueryDTO queryDTO) {
		return find(queryDTO, null);
	}

	@Override
	public GeoSearchWrapper<?, ?> find(GeoDataQueryDTO queryDTO, String parentId) {

		QueryBuilder serviceQuery = GeoDataQueryUtils.getHierarchicalQuery(queryDTO, parentId);

		SearchResponse result = searchRequest(queryDTO, serviceQuery);

		return searchResponseToWrapper(result, getSourceType(GeoSearchWrapper.class));
	}

	@Override
	protected List<?> scrollQueryReturnItems(QueryBuilder builder) {

		return scrollQueryReturnItems(builder, new GeoItemsProcessingFunction<TModel>(typeOfTModel, objectMapper));
	}

	public SimpleQueryDTO createSimpleQueryDTOFromTextQueryParams(Integer from, Integer size) {

		SimpleQueryDTO queryDTO = new SimpleQueryDTO();

		if (from != null)
			queryDTO.setFrom(from);
		if (size != null)
			queryDTO.setSize(size);

		return queryDTO;
	}

	@Override
	protected JavaType getSourceType(Class<?> wrapperClass) {
		return objectMapper.getTypeFactory().constructParametricType(wrapperClass, GeoDataProperties.class,
				Geometry.class);
	}

	public CategoryListDTO getCategories(String parentId, GeoDataQueryDTO queryDTO) {

		HashMap<String, CategoryPathInfo> categoriesPaths = getCategoriesPaths();
		if (categoriesPaths == null)
			return new CategoryListDTO();

		queryDTO.setAggs(getAggsFromCategoriesPaths(categoriesPaths));
		queryDTO.setSize(0);
		GeoSearchWrapper<?, ?> result = find(queryDTO, parentId);

		CategoryListDTO categories = orikaMapper.getMapperFacade().convert(result.getAggregations(),
				CategoryListDTO.class, null, null);

		for (int i = 0; i < categories.size(); i++) {
			String key = categories.get(i).getField();
			CategoryPathInfo values = getCategoriesPaths().get(key);
			if (values != null)
				categories.get(i).setTarget(values.getTarget());
			else {
				LOGGER.debug("No hay target asignado para la categoría {}", key);
				throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
			}
		}

		return categories;
	}

	protected abstract HashMap<String, CategoryPathInfo> getCategoriesPaths();

	protected List<AggsPropertiesDTO> getAggsFromCategoriesPaths(HashMap<String, CategoryPathInfo> categoriesPaths) {

		List<AggsPropertiesDTO> aggs = new ArrayList<>();

		for (Map.Entry<String, CategoryPathInfo> entry : categoriesPaths.entrySet()) {

			CategoryPathInfo values = entry.getValue();

			if (values != null) {
				AggsPropertiesDTO agg = new AggsPropertiesDTO();
				agg.setTerm(entry.getKey());
				agg.setField(values.getPath());

				if (values.getNested() != null)
					agg.setNested(values.getNested());

				agg.setMinCount(1);
				aggs.add(agg);
			}
		}
		return aggs;
	}
}
