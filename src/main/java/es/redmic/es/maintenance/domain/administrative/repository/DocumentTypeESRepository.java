package es.redmic.es.maintenance.domain.administrative.repository;

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

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Repository
public class DocumentTypeESRepository extends RWDataESRepository<DomainES> {
	private static String[] INDEX = { "administrative-domains" };
	private static String TYPE = "documenttype";

	public DocumentTypeESRepository() {
		super(INDEX, TYPE);
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<DomainES> findByName(String name) {

		QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();

		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("name", name));

		return (DataSearchWrapper<DomainES>) findBy(QueryBuilders.boolQuery().must(queryBuilder).filter(filterBuilder));
	}
}
