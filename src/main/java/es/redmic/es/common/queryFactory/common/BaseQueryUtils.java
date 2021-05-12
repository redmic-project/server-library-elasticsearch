package es.redmic.es.common.queryFactory.common;

import java.io.IOException;

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

import org.elasticsearch.common.geo.builders.EnvelopeBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.locationtech.jts.geom.Coordinate;

import es.redmic.exception.elasticsearch.ESBBoxQueryException;
import es.redmic.models.es.common.query.dto.BboxQueryDTO;
import es.redmic.models.es.common.query.dto.DateLimitsDTO;
import es.redmic.models.es.common.query.dto.RegexpDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.common.query.dto.TextQueryDTO;

public abstract class BaseQueryUtils {

	protected BaseQueryUtils() {
		throw new IllegalStateException("Utility class");
	}

	// @formatter:off

	public static final String INTRACK_PATH = "properties.inTrack",
			COLLECT_PATH = "properties.collect",
			SITE_PATH = "properties.site",
			SAMPLINGPLACE_PATH = "properties.samplingPlace",
			MEASUREMENT_PATH = "properties.measurements",

			GEOMETRY_PROPERTY = "geometry",
			DATA_DEFINITION_PROPERTY = "dataDefinition",

			ID_PROPERTY = "id",
			QFLAG_PROPERTY = "qFlag",
			VFLAG_PROPERTY = "vFlag",
			START_DATE_PROPERTY = "startDate",
			END_DATE_PROPERTY = "endDate",
			DATE_PROPERTY = "date",
			RADIUS_PROPERTY = "radius";

	// @formatter:on

	public static <TQueryDTO extends SimpleQueryDTO> BoolQueryBuilder getQuery(TQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {
		return getBaseQuery(queryDTO, internalQuery, partialQuery);
	}

	protected static <TQueryDTO extends SimpleQueryDTO> BoolQueryBuilder getBaseQuery(TQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = QueryBuilders.boolQuery();

		addMustTermIfExist(query, getTextQuery(queryDTO.getText()));
		addMustTermIfExist(query, getRegexpQuery(queryDTO.getRegexp()));
		addMustTermIfExist(query, partialQuery);
		addMustTermIfExist(query, internalQuery);

		return query.hasClauses() ? query : null;
	}

	public static GeoShapeQueryBuilder getBBoxQuery(BboxQueryDTO bbox) {

		return getBBoxQuery(bbox, GEOMETRY_PROPERTY);
	}

	public static GeoShapeQueryBuilder getBBoxQuery(BboxQueryDTO bbox, String property) {

		if (bbox != null && bbox.getBottomRightLat() != null && bbox.getBottomRightLon() != null
				&& bbox.getTopLeftLat() != null && bbox.getTopLeftLon() != null) {

			Coordinate topLeft = new Coordinate(bbox.getTopLeftLon(), bbox.getTopLeftLat());
			Coordinate bottomRight = new Coordinate(bbox.getBottomRightLon(), bbox.getBottomRightLat());

			try {
				return QueryBuilders.geoShapeQuery(property, new EnvelopeBuilder(topLeft, bottomRight));
			} catch (IOException e) {
				throw new ESBBoxQueryException(e);
			}
		}
		return null;
	}

	protected static QueryBuilder getTextQuery(TextQueryDTO queryText) {

		if (queryText == null || queryText.getText() == null || queryText.getSearchFields() == null)
			return null;

		return QueryBuilders.multiMatchQuery(queryText.getText(), queryText.getSearchFields());
	}

	protected static BoolQueryBuilder getRegexpQuery(List<RegexpDTO> regexp) {

		if (regexp == null || regexp.isEmpty())
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

		if (flags == null || flags.isEmpty())
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
