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

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;
import es.redmic.models.es.administrative.model.Program;

@Repository
public class ProgramESRepository extends ActivityCommonESRepository<Program> {

	private static String[] INDEX = { "activity" };
	private static String TYPE = "activity";

	private static QueryBuilder INTERNAL_QUERY = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("rank.id", 1));

	public ProgramESRepository() {
		super(INDEX, TYPE);
	}

	@Override
	public QueryBuilder getInternalQuery() {
		return INTERNAL_QUERY;
	}
}
