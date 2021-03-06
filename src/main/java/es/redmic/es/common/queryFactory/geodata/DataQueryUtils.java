package es.redmic.es.common.queryFactory.geodata;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.common.geo.builders.ShapeBuilders;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;

import com.vividsolutions.jts.geom.Coordinate;

import es.redmic.es.common.queryFactory.common.BaseQueryUtils;
import es.redmic.exception.elasticsearch.ESBBoxQueryException;
import es.redmic.models.es.common.query.dto.BboxQueryDTO;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.PrecisionQueryDTO;
import es.redmic.models.es.common.query.dto.RangeOperator;
import es.redmic.models.es.common.query.dto.ValueQueryDTO;
import es.redmic.models.es.common.query.dto.ZRangeDTO;

public abstract class DataQueryUtils extends BaseQueryUtils {

	// @formatter:off

	public static final String ID_PROPERTY = "uuid", Z_PROPERTY = "z", VALUE_PROPERTY = "value",
			SCRIPT_ENGINE = "groovy", SEARCH_BY_Z_RANGE_SCRIPT = "search-by-z-range",
			SEARCH_NESTED_BY_Z_RANGE_SCRIPT = "search-nested-by-z-range", PARENT = "activity",

			QFLAG_QUERY_FIELD = "qFlags", VFLAG_QUERY_FIELD = "vFlags", ZRANGE_QUERY_FIELD = "z",
			VALUE_QUERY_FIELD = "value", DATELIMIT_QUERY_FIELD = "dateLimits", PRECISION_QUERY_FIELD = "precision";

	// @formatter:on

	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {
		return getGeoDataQuery(queryDTO, internalQuery, partialQuery);
	}

	protected static BoolQueryBuilder getGeoDataQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = getOrInitializeBaseQuery(getBaseQuery(queryDTO, internalQuery, partialQuery));

		addMustTermIfExist(query, getBBoxQuery(queryDTO.getBbox()));

		return getResultQuery(query);
	}

	public static GeoShapeQueryBuilder getBBoxQuery(BboxQueryDTO bbox) {

		if (bbox != null && bbox.getBottomRightLat() != null && bbox.getBottomRightLon() != null
				&& bbox.getTopLeftLat() != null && bbox.getTopLeftLon() != null) {

			Coordinate topLeft = new Coordinate(bbox.getTopLeftLon(), bbox.getTopLeftLat());
			Coordinate bottomRight = new Coordinate(bbox.getBottomRightLon(), bbox.getBottomRightLat());

			try {
				return QueryBuilders.geoShapeQuery("geometry", ShapeBuilders.newEnvelope(topLeft, bottomRight));
			} catch (IOException e) {
				throw new ESBBoxQueryException(e);
			}
		}
		return null;
	}

	protected static QueryBuilder getPrecisionQuery(PrecisionQueryDTO precision) {

		if (precision == null)
			return null;

		return QueryBuilders.rangeQuery(COLLECT_PATH + "." + RADIUS_PROPERTY).from(precision.getMin())
				.to(precision.getMax());
	}

	protected static QueryBuilder getZQuery(String property, String scriptName, ZRangeDTO zRange) {
		return getZQuery(null, property, scriptName, zRange);
	}

	protected static QueryBuilder getZQuery(String basePath, String property, String scriptName, ZRangeDTO zRange) {

		if (zRange == null)
			return null;

		Map<String, Object> scriptParams = new HashMap<String, Object>();
		scriptParams.put("zMin", zRange.getMin());
		scriptParams.put("zMax", zRange.getMax());
		scriptParams.put("basePath", basePath);

		BoolQueryBuilder query = new BoolQueryBuilder();
		query.must(QueryBuilders.existsQuery((basePath != null) ? (basePath + "." + property) : property));
		query.must(QueryBuilders.scriptQuery(new Script(ScriptType.FILE, SCRIPT_ENGINE, scriptName, scriptParams)));

		return query;
	}

	protected static QueryBuilder getZNestedQuery(String nestedPath, String basePath, String property,
			String scriptName, ZRangeDTO zRange) {

		if (zRange == null)
			return null;

		Map<String, Object> scriptParams = new HashMap<String, Object>();
		scriptParams.put("zMin", zRange.getMin());
		scriptParams.put("zMax", zRange.getMax());
		scriptParams.put("basePath", basePath);
		scriptParams.put("nestedPath", nestedPath);

		BoolQueryBuilder query = new BoolQueryBuilder();
		query.must(QueryBuilders.nestedQuery(nestedPath,
				QueryBuilders.existsQuery(nestedPath + "." + basePath + "." + property), ScoreMode.Avg));
		query.must(QueryBuilders.scriptQuery(new Script(ScriptType.FILE, SCRIPT_ENGINE, scriptName, scriptParams)));

		return query;
	}

	protected static QueryBuilder getValueQuery(List<ValueQueryDTO> valueList, String property) {
		return getValueQuery(valueList, null, property);
	}

	protected static QueryBuilder getValueQuery(List<ValueQueryDTO> valueList, String basePath, String property) {

		if (valueList == null || valueList.size() == 0)
			return null;

		String valuePath = (basePath != null) ? (basePath + "." + property) : property;

		BoolQueryBuilder query = QueryBuilders.boolQuery();

		for (ValueQueryDTO item : valueList) {

			if (item.getOperator().equals(RangeOperator.Equal)) {
				query.must(QueryBuilders.matchQuery(valuePath, item.getOp()));
			} else if (item.getOperator().equals(RangeOperator.NotEqual)) {
				query.mustNot(QueryBuilders.matchQuery(valuePath, item.getOp()));
			} else {
				RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(valuePath);
				switch (item.getOperator()) {
				case Greater:
					rangeQuery.gt(item.getOp());
					break;
				case Less:
					rangeQuery.lt(item.getOp());
					break;
				case GreaterOrEqual:
					rangeQuery.gte(item.getOp());
					break;
				case LessOrEqual:
					rangeQuery.lte(item.getOp());
					break;
				default:
					break;
				}
				query.must(rangeQuery);
			}
		}
		return query;
	}

	@SuppressWarnings("serial")
	public static BoolQueryBuilder getItemsQuery(String id, String parentId, List<Long> accessibilityIds) {

		ArrayList<String> ids = new ArrayList<String>() {
			{
				add(id);
			}
		};
		return getItemsQuery(ids, parentId, accessibilityIds);
	}

	public static BoolQueryBuilder getItemsQuery(List<String> ids, String parentId, List<Long> accessibilityIds) {

		BoolQueryBuilder query = QueryBuilders.boolQuery();

		if (accessibilityIds != null && accessibilityIds.size() > 0 && parentId != null)
			query.must(getQueryOnParent(parentId, accessibilityIds));

		else if (accessibilityIds != null && accessibilityIds.size() > 0 && parentId == null)
			query.must(getAccessibilityQueryOnParent(accessibilityIds));

		else if (parentId != null)
			query.must(getQueryByParent(parentId));

		query.must(QueryBuilders.idsQuery().addIds(ids.toArray(new String[ids.size()])));

		return query;
	}

	public static QueryBuilder getHierarchicalQuery(DataQueryDTO queryDTO, String parentId) {

		List<Long> accessibilityIds = queryDTO.getAccessibilityIds();

		if ((accessibilityIds == null || accessibilityIds.size() == 0) && parentId == null)
			return null;

		if (accessibilityIds == null || accessibilityIds.size() == 0)
			return getQueryByParent(parentId);

		if (parentId == null)
			return getAccessibilityQueryOnParent(accessibilityIds);

		return getQueryOnParent(parentId, accessibilityIds);
	}

	public static QueryBuilder getQueryOnParent(String parentId, List<Long> accessibilityIds) {

		if (parentId == null || accessibilityIds == null || accessibilityIds.size() == 0)
			return null;

		return JoinQueryBuilders.hasParentQuery(PARENT, QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery("id", parentId)).must(getAccessibilityQuery(accessibilityIds)), true);
	}

	public static QueryBuilder getQueryByParent(String parentId) {

		if (parentId == null)
			return null;

		return JoinQueryBuilders.hasParentQuery(PARENT, QueryBuilders.termQuery("id", parentId), true);
	}

	public static QueryBuilder getAccessibilityQueryOnParent(List<Long> accessibilityIds) {

		if (accessibilityIds == null || accessibilityIds.size() == 0)
			return null;

		return JoinQueryBuilders.hasParentQuery(PARENT, getAccessibilityQuery(accessibilityIds), true);
	}

	public static QueryBuilder getDocumentQueryOnParent(String documentId) {

		if (documentId == null)
			return null;

		List<String> documentIds = new ArrayList<>();
		documentIds.add(documentId);
		return getDocumentsQueryOnParent(documentIds);
	}

	public static QueryBuilder getDocumentsQueryOnParent(List<String> documentIds) {

		if (documentIds == null || documentIds.size() == 0)
			return null;

		return JoinQueryBuilders.hasParentQuery(PARENT, QueryBuilders.nestedQuery("documents",
				QueryBuilders.termsQuery("documents.document.id", documentIds), ScoreMode.Avg), true);
	}
}
