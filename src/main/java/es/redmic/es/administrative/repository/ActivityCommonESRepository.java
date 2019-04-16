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

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import es.redmic.es.common.queryFactory.data.ActivityQueryUtils;
import es.redmic.es.common.service.UserUtilsServiceItfc;
import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.models.es.administrative.model.ActivityBase;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.data.common.model.DataHitsWrapper;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public abstract class ActivityCommonESRepository<TModel extends ActivityBase> extends RWDataESRepository<TModel> {

	@Autowired
	UserUtilsServiceItfc userService;

	public ActivityCommonESRepository(String[] index, String[] type) {
		super(index, type);
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "name", "name.suggest", "code", "code.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "name", "name.suggest", "code", "code.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "name", "code" };
	}

	/*
	 * Sobrescribe método base para añadir query de control de accesso a datos
	 */
	@Override
	public DataHitWrapper<?> findById(String id) {

		BoolQueryBuilder query = ActivityQueryUtils.getItemsQuery(id, userService.getAccessibilityControl());

		DataSearchWrapper<?> result = findBy(query);

		if (result.getHits() == null || result.getHits().getHits() == null || result.getHits().getHits().size() != 1)
			throw new ItemNotFoundException("id", id);

		return result.getHits().getHits().get(0);
	}

	/*
	 * Sobrescribe método base para añadir query de control de accesso a datos
	 */
	@Override
	public DataHitsWrapper<?> mget(MgetDTO dto) {

		List<String> ids = dto.getIds();

		BoolQueryBuilder query = ActivityQueryUtils.getItemsQuery(ids, userService.getAccessibilityControl());

		DataSearchWrapper<?> result = findBy(query, dto.getFields());

		if (result.getHits() == null || result.getHits().getHits() == null)
			throw new ItemNotFoundException("ids", dto.getIds().toString());

		if (result.getHits().getHits().size() != ids.size()) {

			for (DataHitWrapper<?> hit : result.getHits().getHits()) {
				ids.remove(hit.get_id());
			}

			throw new ItemNotFoundException("ids", ids.toString());
		}

		return result.getHits();
	}
}
