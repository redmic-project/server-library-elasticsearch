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

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.models.es.common.dto.BaseDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.TextQueryDTO;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public class SettingsRepository<TModel extends BaseES<?>, TDTO extends BaseDTO<?>> extends SettingsBaseRepository<TModel> {

	public SettingsRepository(String[] index, String type) {
		super(index, type);
	}

	public DataSearchWrapper<?> findByUserAndSearch(String user, String service, DataQueryDTO dto) {

		BoolQueryBuilder filter = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("userId", user))
				.must(QueryBuilders.termQuery("service", service));

		QueryBuilder query = null;
		if (dto.getText() != null) {
			TextQueryDTO text = dto.getText();
			if (text.getText() != null) {
				query = QueryBuilders.multiMatchQuery(text.getText(), text.getSearchFields());
			}
		} else
			query = QueryBuilders.matchAllQuery();

		return findBy(QueryBuilders.boolQuery().must(query).filter(filter));
	}
}
