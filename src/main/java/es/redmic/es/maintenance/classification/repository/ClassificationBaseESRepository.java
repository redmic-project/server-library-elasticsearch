package es.redmic.es.maintenance.classification.repository;

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

import es.redmic.es.common.repository.HierarchicalESRepository;
import es.redmic.models.es.common.dto.BaseDTO;
import es.redmic.models.es.common.model.BaseES;

public abstract class ClassificationBaseESRepository<TModel extends BaseES<?>, TDTO extends BaseDTO<?>> extends HierarchicalESRepository<TModel, TDTO> {

	protected ClassificationBaseESRepository() {
		super();
	}

	protected ClassificationBaseESRepository(String[] index, String type) {
		super(index, type);
	}

	@Override
	protected String getMappingFilePath(String index, String type) {
		return MAPPING_BASE_PATH + "classifications/classification" + MAPPING_FILE_EXTENSION;
	}

	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {


		if (terms.containsKey("level")) {

			Integer level = (Integer) terms.get("level");
			query.filter(QueryBuilders.termQuery("level", level));
		}
		if (terms.containsKey("parentPath")) {

			String path = (String) terms.get("parentPath");
			Integer level = path.split("\\.").length;

			BoolQueryBuilder filter = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("path.path", path));

			Boolean returnChildren = true;
			if (terms.containsKey("children"))
				returnChildren = (Boolean) terms.get("children");

			if (returnChildren != null && !returnChildren)
				filter.must(QueryBuilders.termQuery("level", level));
			else
				filter.must(QueryBuilders.rangeQuery("level").gte(level));

			query.filter(filter);
		}

		return super.getTermQuery(terms, query);
	}
}
