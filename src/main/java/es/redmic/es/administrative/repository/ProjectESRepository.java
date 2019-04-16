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

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.service.UserUtilsServiceItfc;
import es.redmic.models.es.administrative.model.Project;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Repository
public class ProjectESRepository extends ActivityCommonESRepository<Project> {

	private static String[] INDEX = { "activity" };
	private static String[] TYPE = { "project" };

	@Autowired
	UserUtilsServiceItfc userService;

	public ProjectESRepository() {
		super(INDEX, TYPE);
	}

	/**
	 * Función que sobrescribe a getTermQuery de RElasticSearchRepository para
	 * añadir implementación específica para crear una query a apartir de una
	 * serie de términos obtenidos por el controlador.
	 */
	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("path.split")) {
			query.must(QueryBuilders.termsQuery("path.split", terms.get("path.split")));
		}
		return super.getTermQuery(terms, query);
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Project> findByParent(String programId) {

		QueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("path.split", programId));

		return (DataSearchWrapper<Project>) findBy(QueryBuilders.boolQuery().must(query));
	}
}
