package es.redmic.es.administrative.repository;

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

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.service.UserUtilsServiceItfc;
import es.redmic.models.es.administrative.model.Activity;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Repository
public class ActivityESRepository extends ActivityCommonESRepository<Activity> {

	private static String[] INDEX = { "activity" };
	private static String TYPE = "activity";

	protected static String CHILDREN_NAME = "geodata";

	private static QueryBuilder INTERNAL_QUERY = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("rank.id", 3));

	@Autowired
	UserUtilsServiceItfc userService;

	public ActivityESRepository() {
		super(INDEX, TYPE);
	}

	@Override
	public QueryBuilder getInternalQuery() {
		return INTERNAL_QUERY;
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Activity> findByGrandParent(String programId) {

		QueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("path.split", programId))
				.must(QueryBuilders.termQuery("rank.id", 3));

		return (DataSearchWrapper<Activity>) findBy(QueryBuilders.boolQuery().must(query));
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Activity> findByParent(String projectId) {

		QueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("path.split", projectId));

		return (DataSearchWrapper<Activity>) findBy(QueryBuilders.boolQuery().must(query));
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Activity> findBySpecies(DataQueryDTO query, String speciesId) {

		QueryBuilder queryBuilder = getOrInitializeQuery(query, getInternalQuery(), getTermQuery(query.getTerms()));

		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery()
				.must(JoinQueryBuilders.hasChildQuery(CHILDREN_NAME, QueryBuilders.boolQuery()
						.should(QueryBuilders.termQuery("properties.collect.taxon.path.split", speciesId))
						.should(QueryBuilders.termQuery("properties.collect.taxon.validAs.path.split", speciesId))
						.should(QueryBuilders.termQuery(
								"properties.collect.misidentification.goodIdentification.path.split", speciesId)),
						ScoreMode.Avg));

		return (DataSearchWrapper<Activity>) findBy(QueryBuilders.boolQuery().must(queryBuilder).filter(filterBuilder));
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Activity> findByOrganisations(DataQueryDTO query, Long organisationId) {

		QueryBuilder queryBuilder = getOrInitializeQuery(query, getInternalQuery(), getTermQuery(query.getTerms()));

		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery().must(QueryBuilders.nestedQuery("organisations",
				QueryBuilders.termQuery("organisations.organisation.id", organisationId), ScoreMode.Avg));

		return (DataSearchWrapper<Activity>) findBy(QueryBuilders.boolQuery().must(queryBuilder).filter(filterBuilder));
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Activity> findByPlatforms(DataQueryDTO query, Long platformId) {

		QueryBuilder queryBuilder = getOrInitializeQuery(query, getInternalQuery(), getTermQuery(query.getTerms()));

		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery().must(QueryBuilders.nestedQuery("platforms",
				QueryBuilders.termQuery("platforms.platform.id", platformId), ScoreMode.Avg));

		return (DataSearchWrapper<Activity>) findBy(QueryBuilders.boolQuery().must(queryBuilder).filter(filterBuilder));
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Activity> findByDocuments(DataQueryDTO query, Long documentId) {

		QueryBuilder queryBuilder = getOrInitializeQuery(query, getInternalQuery(), getTermQuery(query.getTerms()));

		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery().must(QueryBuilders.nestedQuery("documents",
				QueryBuilders.termQuery("documents.document.id", documentId), ScoreMode.Avg));

		return (DataSearchWrapper<Activity>) findBy(QueryBuilders.boolQuery().must(queryBuilder).filter(filterBuilder));
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Activity> findByContacts(DataQueryDTO query, Long contactId) {

		QueryBuilder queryBuilder = getOrInitializeQuery(query, getInternalQuery(), getTermQuery(query.getTerms()));

		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery().must(QueryBuilders.nestedQuery("contacts",
				QueryBuilders.termQuery("contacts.contact.id", contactId), ScoreMode.Avg));

		return (DataSearchWrapper<Activity>) findBy(QueryBuilders.boolQuery().must(queryBuilder).filter(filterBuilder));
	}

	private QueryBuilder getOrInitializeQuery(DataQueryDTO query, QueryBuilder internalQuery,
			QueryBuilder filterQuery) {

		QueryBuilder queryBuilder = getQuery(query, internalQuery, filterQuery);
		if (queryBuilder == null)
			queryBuilder = QueryBuilders.matchAllQuery();
		return queryBuilder;
	}

	/**
	 * Función que sobrescribe a getTermQuery de RElasticSearchRepository para
	 * añadir implementación específica para crear una query a apartir de una
	 * serie de términos obtenidos por el controlador.
	 */

	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("misidentification")) {
			query.should(JoinQueryBuilders.hasChildQuery(CHILDREN_NAME,
					QueryBuilders.boolQuery()
							.must(QueryBuilders.termQuery("properties.collect.misidentification.id",
									terms.get("misidentification")))
							.must(QueryBuilders.termQuery("properties.collect.id", DataPrefixType.CITATION)),
					ScoreMode.Avg));
		}
		if (terms.containsKey("activityType.id")) {
			String ids = (String) terms.get("activityType.id");
			query.must(QueryBuilders.termsQuery("activityType.id", ids.split(",")));
		}
		if (terms.containsKey("activityCategory")) {
			String ids = (String) terms.get("activityCategory");
			query.must(QueryBuilders.termsQuery("activityCategory", ids.split(",")));
		}
		if (terms.containsKey("path.split")) {
			query.must(QueryBuilders.termsQuery("path.split", terms.get("path.split")));
		}
		return super.getTermQuery(terms, query);
	}
}
