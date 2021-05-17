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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.utils.ElasticPersistenceUtils;
import es.redmic.models.es.common.dto.SelectionWorkDTO;
import es.redmic.models.es.common.model.Selection;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Repository
public class SelectionWorkRepository extends SettingsBaseRepository<Selection> {

	@Autowired
	ElasticPersistenceUtils<Selection> elasticPersistenceUtils;

	public static String[] INDEX = { "selectionwork" };
	public static String TYPE = "_doc";

	public SelectionWorkRepository() {
		super(INDEX, TYPE);
	}

	public DataSearchWrapper<?> findByUser(String user, String service) {

		BoolQueryBuilder filter = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("userId", user))
				.must(QueryBuilders.termQuery("service", service));

		return findBy(QueryBuilders.boolQuery().filter(filter));
	}

	@SuppressWarnings("unchecked")
	public List<String> getSelectedIds(String selectionId) {

		DataHitWrapper<Selection> result = (DataHitWrapper<Selection>) findById(selectionId);
		Selection selection = result.get_source();
		if (selection != null)
			return selection.getIds();
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public SelectionWorkDTO updateSelection(Selection model, String script) {

		List<UpdateResponse> updateResponse = elasticPersistenceUtils.updateByBulk(elasticPersistenceUtils
				.getUpdateScript(INDEX, TYPE, model.getId(), objectMapper.convertValue(model, Map.class), script));

		return objectMapper.convertValue(updateResponse.get(0).getGetResult().getSource(), SelectionWorkDTO.class);

	}
}
