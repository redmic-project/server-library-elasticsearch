package es.redmic.es.maintenance.domain.administrative.taxonomy.repository;

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
import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.common.model.DomainES;

@Repository
public class TaxonRankESRepository extends DomainESRepository<DomainES> {
	private static String[] INDEX = { "taxon-domains" };
	private static String TYPE = "rank";

	public TaxonRankESRepository() {
		super(INDEX, TYPE);
	}

	@Override
	/*
	 * Función que sobrescribe a getTermQuery de RElasticSearchRepository para
	 * añadir implementación específica para crear una query a apartir de una
	 * serie de términos obtenidos por el controlador.
	 */
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("id")) {
			query.filter(QueryBuilders.rangeQuery("id").gte(terms.get("id")));
		}
		return super.getTermQuery(terms, query);
	}
}
