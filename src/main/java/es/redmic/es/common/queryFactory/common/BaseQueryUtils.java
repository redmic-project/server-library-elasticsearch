package es.redmic.es.common.queryFactory.common;

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

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.DateLimitsDTO;
import es.redmic.models.es.common.query.dto.RegexpDTO;
import es.redmic.models.es.common.query.dto.TextQueryDTO;

public abstract class BaseQueryUtils {

	// @formatter:off

	public static final String INTRACK_PATH = "properties.inTrack",
			COLLECT_PATH = "properties.collect",
			SITE_PATH = "properties.site",
			SAMPLINGPLACE_PATH = "properties.samplingPlace",
			MEASUREMENT_PATH = "properties.measurements",
			
			DATA_DEFINITION_PROPERTY = "dataDefinition",
			
			ID_PROPERTY = "id",
			QFLAG_PROPERTY = "qFlag",
			VFLAG_PROPERTY = "vFlag",
			START_DATE_PROPERTY = "startDate",
			END_DATE_PROPERTY = "endDate",
			DATE_PROPERTY = "date",
			RADIUS_PROPERTY = "radius";

	// @formatter:on

	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {
		return getBaseQuery(queryDTO, internalQuery, partialQuery);
	}

	protected static BoolQueryBuilder getBaseQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = QueryBuilders.boolQuery();

		addMustTermIfExist(query, getTextQuery(queryDTO.getText()));
		addMustTermIfExist(query, getRegexpQuery(queryDTO.getRegexp()));
		addMustTermIfExist(query, partialQuery);
		addMustTermIfExist(query, internalQuery);

		return query.hasClauses() ? query : null;
	}

	protected static QueryBuilder getTextQuery(TextQueryDTO queryText) {

		if (queryText == null || queryText.getText() == null || queryText.getSearchFields() == null)
			return null;

		return QueryBuilders.multiMatchQuery(queryText.getText(), queryText.getSearchFields());
	}

	protected static BoolQueryBuilder getRegexpQuery(List<RegexpDTO> regexp) {

		if (regexp == null || regexp.size() < 1)
			return null;

		BoolQueryBuilder regexpQuery = QueryBuilders.boolQuery();

		int size = regexp.size();
		for (int i = 0; i < size; i++)
			regexpQuery.must(QueryBuilders.regexpQuery(regexp.get(i).getField(), regexp.get(i).getExp()));

		return regexpQuery;
	}

	public static QueryBuilder getAccessibilityQuery(List<Long> accessibilityIds) {

		if (accessibilityIds == null)
			return null;
		return QueryBuilders.termsQuery("accessibility.id", accessibilityIds);
	}

	protected static QueryBuilder getFlagQuery(List<String> flags, String propertyPath) {

		if (flags == null || flags.size() == 0)
			return null;

		return QueryBuilders.termsQuery(propertyPath, flags);
	}

	protected static QueryBuilder getDateLimitsQuery(DateLimitsDTO dateLimitsDTO, String datePath) {

		if (dateLimitsDTO == null)
			return null;

		RangeQueryBuilder range = QueryBuilders.rangeQuery(datePath);

		if (dateLimitsDTO.getStartDate() != null)
			range.gte(dateLimitsDTO.getStartDate());
		if (dateLimitsDTO.getEndDate() != null)
			range.lte(dateLimitsDTO.getEndDate());

		return range;
	}

	protected static QueryBuilder getDateLimitsQuery(DateLimitsDTO dateLimitsDTO, String startDatePath,
			String endDatePath) {

		if (dateLimitsDTO == null)
			return null;

		BoolQueryBuilder query = QueryBuilders.boolQuery();

		if (dateLimitsDTO.getStartDate() != null && startDatePath != null)
			query.must(QueryBuilders.rangeQuery(startDatePath).gte(dateLimitsDTO.getStartDate()));

		if (dateLimitsDTO.getEndDate() != null)
			query.must(QueryBuilders.rangeQuery(endDatePath).lte(dateLimitsDTO.getEndDate()));

		return query;
	}

	protected static void addMustTermIfExist(BoolQueryBuilder baseQuery, QueryBuilder term) {

		if (term != null)
			baseQuery.must(term);
	}

	protected static BoolQueryBuilder getOrInitializeBaseQuery(BoolQueryBuilder baseQuery) {

		if (baseQuery == null)
			baseQuery = QueryBuilders.boolQuery();
		return baseQuery;
	}

	protected static BoolQueryBuilder getResultQuery(BoolQueryBuilder query) {

		if (query.hasClauses())
			return query;
		return null;
	}
}
