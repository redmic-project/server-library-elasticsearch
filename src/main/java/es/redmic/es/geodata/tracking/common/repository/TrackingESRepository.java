package es.redmic.es.geodata.tracking.common.repository;

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

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BaseAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.queryFactory.geodata.DataQueryUtils;
import es.redmic.es.common.queryFactory.geodata.TrackingQueryUtils;
import es.redmic.models.es.common.query.dto.AggsPropertiesDTO;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.request.dto.CategoryPathInfo;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;

@Repository
public class TrackingESRepository extends ClusterTrackingESRepository<GeoPointData> {
	
	@Value("${controller.mapping.TAXONS}")
	private String TAXONS_TARGET;
	
	@Value("${controller.mapping.ANIMAL}")
	private String ANIMAL_TARGET;
	
	@Value("${controller.mapping.DEVICE}")
	private String DEVICE_TARGET;
	
	@Value("${controller.mapping.PLATFORM}")
	private String PLATFORM_TARGET;

	public TrackingESRepository() {
		super();
		setInternalQuery(TrackingQueryUtils.INTERNAL_QUERY);
	}

	@Override
	protected List<BaseAggregationBuilder> getAggs(DataQueryDTO elasticQueryDTO) {

		List<AggsPropertiesDTO> aggs = elasticQueryDTO.getAggs();

		if (aggs != null && aggs.size() > 0 && aggs.get(0).getField().equals("elements")) {
			List<BaseAggregationBuilder> aggsBuilder = new ArrayList<BaseAggregationBuilder>();

			aggsBuilder.add(AggregationBuilders.terms("animal").field("properties.collect.animal.id")
					.order(Order.term(true)).size(MAX_SIZE));
			aggsBuilder.add(AggregationBuilders.terms("platform").field("properties.inTrack.platform.id")
					.order(Order.term(true)).size(MAX_SIZE));

			return aggsBuilder;
		} else
			return super.getAggs(elasticQueryDTO);
	}

	/*
	 * Devuelve todos los puntos de un track para un elemento dado
	 */
	public GeoSearchWrapper<?, ?> find(String parentId, String uuid, DataQueryDTO queryDTO) {

		QueryBuilder serviceQuery = DataQueryUtils.getHierarchicalQuery(queryDTO, parentId);

		BoolQueryBuilder builder = QueryBuilders.boolQuery()
				.filter(QueryBuilders.boolQuery()
						.must(QueryBuilders.boolQuery().should(QueryBuilders.termQuery(PLATFORM_PATH, uuid))
								.should(QueryBuilders.termQuery(ANIMAL_PATH, uuid))));

		SearchResponse result = searchRequest(queryDTO, builder.must(serviceQuery));

		return searchResponseToWrapper(result, getSourceType(GeoSearchWrapper.class));
	}

	@Override
	public HashMap<String, CategoryPathInfo> getCategoriesPaths() {
		
		// TODO: usar el diccionario de dto a model cuando esté implementado
		HashMap<String, CategoryPathInfo> categoriesPaths = new HashMap<>();
		categoriesPaths.put("properties.taxon.id", new CategoryPathInfo("properties.collect.taxon.path", TAXONS_TARGET));
		categoriesPaths.put("properties.animal.id", new CategoryPathInfo("properties.collect.animal.id", ANIMAL_TARGET));
		categoriesPaths.put("properties.device.id", new CategoryPathInfo("properties.inTrack.device.id", DEVICE_TARGET));
		categoriesPaths.put("properties.platform.id", new CategoryPathInfo("properties.inTrack.platform.id", PLATFORM_TARGET));

		return categoriesPaths;
	}
}
