package es.redmic.es.geodata.common.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

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

import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.sort.SortBuilders;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Repository;

import es.redmic.models.es.common.request.dto.CategoryPathInfo;
import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;
import es.redmic.models.es.geojson.common.model.Properties;

@Repository
public class SpeciesGeoESRepository extends GeoPresenceESRepository<GeoPointData> {

	public SpeciesGeoESRepository() {
		super();
	}

	@SuppressWarnings({ "unchecked" })
	public List<String> getActivitiesBySpecies(String speciesId) {

		List<String> activities = new ArrayList<>();

		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.boolQuery()
						.should(QueryBuilders.termQuery("properties.collect.taxon.path.split", speciesId))
						.should(QueryBuilders.termQuery("properties.collect.taxon.validAs.path.split", speciesId))
						.should(QueryBuilders.termQuery(
								"properties.collect.misidentification.goodIdentification.path.split", speciesId)));

		List<String> returnFields = new ArrayList<>();
		returnFields.add("properties.activityId");
		GeoSearchWrapper<Properties, Geometry> result =
			(GeoSearchWrapper<Properties, Geometry>) findBy(
				QueryBuilders.boolQuery().filter(filterBuilder),
				SortBuilders.fieldSort("properties.activityId").order(SortOrder.DESC),
				returnFields);

		if (result == null || result.getTotal() == 0)
			return activities;

 		List<GeoHitWrapper<Properties, Geometry>> hits = result.getHits().getHits();

		for (GeoHitWrapper<Properties, Geometry> item: hits) {

			if (item != null && item.get_source() != null && item.get_source().getProperties() != null
				&& item.get_source().getProperties().getActivityId() != null) {
					activities.add(item.get_source().getProperties().getActivityId());
			}
		}
		// ELimina duplicados
		return new ArrayList<>(new LinkedHashSet<>(activities));
	}

	/**
	 * Función que sobrescribe a getTermQuery de RElasticSearchRepository para
	 * añadir implementación específica para crear una query a apartir de una
	 * serie de términos obtenidos por el controlador.
	 */

	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		// TODO
		return super.getTermQuery(terms, query);
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] {
				"properties.collect.taxon.scientificName",
				"properties.collect.taxon.scientificName.suggest",
				"properties.collect.taxon.authorship",
				"properties.collect.taxon.authorship.suggest",
				"properties.collect.taxon.commonName",
				"properties.collect.taxon.commonName.suggest",

				"properties.collect.taxon.validAs.scientificName",
				"properties.collect.taxon.validAs.scientificName.suggest",
				"properties.collect.taxon.validAs.authorship",
				"properties.collect.taxon.validAs.authorship.suggest",
				"properties.collect.taxon.validAs.commonName",
				"properties.collect.taxon.validAs.commonName.suggest",

				"properties.collect.taxon.missidentification.scientificName",
				"properties.collect.taxon.missidentification.scientificName.suggest",
				"properties.collect.taxon.misidentification.authorship",
				"properties.collect.taxon.misidentification.authorship.suggest",
				"properties.collect.taxon.misidentification.commonName",
				"properties.collect.taxon.misidentification.commonName.suggest"
				};
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] {
				"properties.collect.taxon.scientificName.suggest",
				"properties.collect.taxon.authorship.suggest",
				"properties.collect.taxon.commonName.suggest"
				};
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] {
				"properties.collect.taxon.scientificName",
				"properties.collect.taxon.authorship",
				"properties.collect.taxon.commonName",

				"properties.collect.taxon.validAs.scientificName",
				"properties.collect.taxon.validAs.authorship",
				"properties.collect.taxon.validAs.commonName",

				"properties.collect.taxon.missidentification.scientificName",
				"properties.collect.taxon.misidentification.authorship",
				"properties.collect.taxon.misidentification.commonName"
				};
	}

	@Override
	protected HashMap<String, CategoryPathInfo> getCategoriesPaths() {
		// TODO Auto-generated method stub
		return null;
	}
}
