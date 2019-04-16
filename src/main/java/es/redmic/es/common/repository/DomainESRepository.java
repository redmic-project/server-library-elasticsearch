package es.redmic.es.common.repository;

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

import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public class DomainESRepository<TModel extends BaseES<Long>> extends RWDataESRepository<TModel> {

	public DomainESRepository(String[] index, String[] type) {
		super(index, type);
	}

	public DataSearchWrapper<?> findByName(String name) {

		return findBy(QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("name", name)));
	}

	public DataSearchWrapper<?> findByName_en(String name) {

		return findBy(QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("name_en", name)));
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "name", "name.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "name", "name.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "name" };
	}
}
