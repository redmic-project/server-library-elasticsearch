package es.redmic.es.common.queryFactory.data;

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
import java.util.List;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.joda.time.DateTime;

import es.redmic.es.common.queryFactory.common.BaseQueryUtils;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.DateLimitsDTO;

public abstract class ActivityQueryUtils extends BaseQueryUtils {

	public static final String START_DATE_FIELD = "startDate";
	public static final String END_DATE_FIELD = "endDate";
	public static final String GEOMETRY_PROPERTY = "spatialExtension";

	protected ActivityQueryUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = getOrInitializeBaseQuery(getBaseQuery(queryDTO, internalQuery, partialQuery));

		if (queryDTO.getAccessibilityIds() != null && !queryDTO.getAccessibilityIds().isEmpty())
			query.must(getAccessibilityQuery(queryDTO.getAccessibilityIds()));

		if (queryDTO.getDateLimits() != null) {
			query.must(getMetadataDateLimitsQuery(queryDTO.getDateLimits()));
		}

		if (queryDTO.getBbox() != null) {
			query.must(getBBoxQuery(queryDTO.getBbox(), GEOMETRY_PROPERTY));
		}

		if (queryDTO.getStarred() != null) {
			query.must(QueryBuilders.termsQuery("starred", queryDTO.getStarred()));
		}

		if (queryDTO.getHasResource() != null) {

			NestedQueryBuilder resourceExistQuery =
				QueryBuilders.nestedQuery("resources", QueryBuilders.existsQuery("resources.name"), ScoreMode.Avg);

			if (Boolean.TRUE.equals(queryDTO.getHasResource())) {
				query.must(resourceExistQuery);
			}
			else {
				query.mustNot(resourceExistQuery);
			}
		}

		if (queryDTO.getResource() != null) {
			query.must(QueryBuilders.nestedQuery("resources", QueryBuilders.termQuery("resources.id",
				queryDTO.getResource()), ScoreMode.Avg));
		}

		if (queryDTO.getDocument() != null) {
			query.must(QueryBuilders.nestedQuery("documents", QueryBuilders.termQuery("documents.document.id",
				queryDTO.getDocument()), ScoreMode.Avg));
		}

		if (queryDTO.getContact() != null) {
			query.must(QueryBuilders.nestedQuery("contacts", QueryBuilders.termQuery("contacts.contact.id",
					queryDTO.getContact()), ScoreMode.Avg));
		}

		if (queryDTO.getOrganisation() != null) {
			query.must(QueryBuilders.nestedQuery("organisations", QueryBuilders.termQuery("organisations.organisation.id",
				queryDTO.getOrganisation()), ScoreMode.Avg));
		}

		if (queryDTO.getPlatform() != null) {
			query.must(QueryBuilders.nestedQuery("platforms", QueryBuilders.termQuery("platforms.platform.id",
				queryDTO.getPlatform()), ScoreMode.Avg));
		}

		if (queryDTO.getProject() != null) {
			query.must(QueryBuilders.termQuery("parent.id", queryDTO.getProject()));
		}

		if (queryDTO.getProgram() != null) {
			query.must(QueryBuilders.termQuery("grandparent.id", queryDTO.getProgram()));
		}

		Integer status = queryDTO.getStatus();
		if (status != null) {
			if (status.equals(1)) {
				BoolQueryBuilder statusQuery = QueryBuilders.boolQuery()
					.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(START_DATE_PROPERTY)))
					.should(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery(START_DATE_PROPERTY).gt(DateTime.now())));
				query.must(statusQuery);
			} else if (status.equals(2)) {
				query.mustNot(QueryBuilders.existsQuery(END_DATE_PROPERTY));
			} else if (status.equals(3)) {
				query.must(QueryBuilders.existsQuery(END_DATE_PROPERTY));
			}
		}

		return getResultQuery(query);
	}

	protected static QueryBuilder getMetadataDateLimitsQuery(DateLimitsDTO dateLimitsDTO) {

		if (dateLimitsDTO == null)
			return null;

		BoolQueryBuilder metadataDateLimitsQuery = QueryBuilders.boolQuery();

		if (dateLimitsDTO.getStartDate() != null && dateLimitsDTO.getEndDate() == null) {
			metadataDateLimitsQuery.must(QueryBuilders.boolQuery()
				.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(END_DATE_FIELD)))
				.should(QueryBuilders.rangeQuery(END_DATE_FIELD).gte(dateLimitsDTO.getStartDate())));
		}
		else if (dateLimitsDTO.getStartDate() != null && dateLimitsDTO.getEndDate() != null) {
			metadataDateLimitsQuery.must(QueryBuilders.boolQuery()
				.should(QueryBuilders.boolQuery()
					.must(QueryBuilders.rangeQuery(START_DATE_FIELD).gte(dateLimitsDTO.getStartDate()))
					.must(QueryBuilders.rangeQuery(START_DATE_FIELD).lte(dateLimitsDTO.getEndDate())))
				.should(QueryBuilders.boolQuery()
					.must(QueryBuilders.rangeQuery(END_DATE_FIELD).gte(dateLimitsDTO.getStartDate()))
					.must(QueryBuilders.rangeQuery(END_DATE_FIELD).lte(dateLimitsDTO.getEndDate())))
				.should(QueryBuilders.boolQuery()
					.must(QueryBuilders.rangeQuery(START_DATE_FIELD).lte(dateLimitsDTO.getStartDate()))
					.must(QueryBuilders.rangeQuery(END_DATE_FIELD).gte(dateLimitsDTO.getEndDate())))
				.should(QueryBuilders.boolQuery()
					.mustNot(QueryBuilders.existsQuery(END_DATE_FIELD))
					.must(QueryBuilders.rangeQuery(START_DATE_FIELD).lte(dateLimitsDTO.getEndDate()))));
		}
		else {
			metadataDateLimitsQuery.must(QueryBuilders.boolQuery()
				.should(QueryBuilders.boolQuery()
					.mustNot(QueryBuilders.existsQuery(END_DATE_FIELD))
					.must(QueryBuilders.rangeQuery(START_DATE_FIELD).lte(dateLimitsDTO.getEndDate())))
				.should(QueryBuilders.rangeQuery(END_DATE_FIELD).lte(dateLimitsDTO.getEndDate())));
		}
		return metadataDateLimitsQuery;
	}



	public static BoolQueryBuilder getItemsQuery(String id, List<Long> accessibilityIds) {

		ArrayList<String> ids = new ArrayList<>();
		ids.add(id);
		return getItemsQuery(ids, accessibilityIds);
	}

	public static BoolQueryBuilder getItemsQuery(List<String> ids, List<Long> accessibilityIds) {

		BoolQueryBuilder query = QueryBuilders.boolQuery();
		if (accessibilityIds != null && !accessibilityIds.isEmpty())
			query.must(getAccessibilityQuery(accessibilityIds));

		query.must(QueryBuilders.idsQuery().addIds(ids.toArray(new String[ids.size()])));
		return query;
	}
}
