package es.redmic.es.administrative.taxonomy.repository;

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
import org.springframework.stereotype.Repository;

import es.redmic.models.es.administrative.taxonomy.dto.SpeciesDTO;
import es.redmic.models.es.administrative.taxonomy.model.Species;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Repository
public class SpeciesESRepository extends HierarchicalTaxonomyESRepository<Species, SpeciesDTO> {

	private static QueryBuilder INTERNAL_QUERY = QueryBuilders.boolQuery()
			.must(QueryBuilders.rangeQuery("rank.id").gte(10));

	public SpeciesESRepository() {
		super();
	}

	@Override
	public QueryBuilder getInternalQuery() {
		return INTERNAL_QUERY;
	}

	@Override
	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Species> findByAscendants(List<String> pathParent) {

		QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();

		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.termsQuery("path.split", pathParent))
				.must(QueryBuilders.rangeQuery("rank.id").gte(10)).must(QueryBuilders.termQuery("leaves", 0));

		return (DataSearchWrapper<Species>) findBy(QueryBuilders.boolQuery().must(queryBuilder).filter(filterBuilder));
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "scientificName", "commonName", "peculiarity.popularNames", "scientificName.suggest",
				"commonName.suggest", "peculiarity.popularNames.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "scientificName", "commonName", "peculiarity.popularNames", "scientificName.suggest",
				"commonName.suggest", "peculiarity.popularNames.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "scientificName", "aphia", "commonName", "peculiarity.popularNames" };
	}
}
