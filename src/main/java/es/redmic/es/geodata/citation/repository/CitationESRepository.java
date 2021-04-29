package es.redmic.es.geodata.citation.repository;

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
import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.queryFactory.geodata.CitationQueryUtils;
import es.redmic.es.common.queryFactory.geodata.DataQueryUtils;
import es.redmic.es.geodata.common.repository.GeoPresenceESRepository;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.request.dto.CategoryPathInfo;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;

@Repository
public class CitationESRepository extends GeoPresenceESRepository<GeoPointData> {

	@Value("${controller.mapping.TAXONS}")
	private String TAXONS_TARGET;

	@Value("${controller.mapping.CONFIDENCE}")
	private String CONFIDENCE_TARGET;

	public CitationESRepository() {
		super();
		setInternalQuery(CitationQueryUtils.INTERNAL_QUERY);
	}

	@SuppressWarnings("unchecked")
	public GeoSearchWrapper<GeoDataProperties, Point> findByDocument(DataQueryDTO queryDTO, String documentId) {

		QueryBuilder termQuery = getTermQuery(queryDTO.getTerms());
		QueryBuilder queryBuilder = getQuery(queryDTO, getInternalQuery(), termQuery);
		if (queryBuilder == null)
			queryBuilder = QueryBuilders.matchAllQuery();

		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery()
				.must(DataQueryUtils.getDocumentQueryOnParent(documentId));

		return (GeoSearchWrapper<GeoDataProperties, Point>) findBy(
				QueryBuilders.boolQuery().must(queryBuilder).filter(filterBuilder));
	}

	@SuppressWarnings("unchecked")
	public GeoSearchWrapper<GeoDataProperties, Point> findByMisidentification(String misidentificationId) {

		QueryBuilder termQuery = QueryBuilders.termQuery("properties.collect.misidentification.id",
				misidentificationId), queryBuilder = getQuery(new DataQueryDTO(), getInternalQuery(), termQuery);

		return (GeoSearchWrapper<GeoDataProperties, Point>) findBy(QueryBuilders.boolQuery().must(queryBuilder));
	}

	/**
	 * Función que sobrescribe a getTermQuery de RElasticSearchRepository para
	 * añadir implementación específica para crear una query a apartir de una
	 * serie de términos obtenidos por el controlador.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("documents") && terms.containsKey("taxon")) {
			query.must(DataQueryUtils.getDocumentsQueryOnParent((ArrayList<String>) terms.get("documents")))
					.must(QueryBuilders.termQuery("properties.collect.taxon.path.split", terms.get("taxon")));
		}
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
	public HashMap<String, CategoryPathInfo> getCategoriesPaths() {

		// TODO: usar el diccionario de dto a model cuando esté implementado
		HashMap<String, CategoryPathInfo> categoriesPaths = new HashMap<>();
		categoriesPaths.put("properties.taxon.id", new CategoryPathInfo("properties.collect.taxon.path", TAXONS_TARGET));
		categoriesPaths.put("properties.confidence.id", new CategoryPathInfo("properties.collect.confidence.id", CONFIDENCE_TARGET));
		return categoriesPaths;
	}
}
