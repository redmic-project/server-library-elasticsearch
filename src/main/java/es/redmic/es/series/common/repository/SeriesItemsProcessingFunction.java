package es.redmic.es.series.common.repository;

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

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.es.common.repository.IProcessItemFunction;
import es.redmic.models.es.series.common.model.SeriesCommon;

public class SeriesItemsProcessingFunction<TModel extends SeriesCommon> implements IProcessItemFunction<TModel> {
	
	List<TModel> items = new ArrayList<TModel>();
	Class<TModel> typeOfTModel;
	protected ObjectMapper objectMapper;
	
	public SeriesItemsProcessingFunction(Class<TModel> typeOfTModel, ObjectMapper objectMapper) {
		this.typeOfTModel = typeOfTModel;
		this.objectMapper = objectMapper;
	}

	@Override
	public void process(SearchHit hit) {
		TModel item = mapper(hit);
		items.add(item);
	}
	
	private TModel mapper(SearchHit hit) {
		
		TModel item = objectMapper.convertValue(hit.getSourceAsMap(), this.typeOfTModel);
		SearchHitField parent = (SearchHitField) hit.getFields().get("_parent");
		item.set_parentId(parent.getValue().toString());
		SearchHitField grandParent = (SearchHitField) hit.getFields().get("_routing");
		item.set_grandparentId(grandParent.getValue().toString());
		return item;
	}

	@Override
	public List<?> getResults() {
		return items;
	}

}
