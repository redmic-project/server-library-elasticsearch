package es.redmic.es.geodata.common.repository;

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
import java.util.Map;

import org.elasticsearch.action.update.UpdateRequest;

import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.geojson.common.model.Feature;

public interface IRWGeoDataESRepository<TModel extends Feature<?, ?>> {

	public TModel save(TModel modelToIndex);

	public TModel update(TModel modelToIndex);

	public Boolean delete(String id, String parentId);

	public List<ReferencesES<TModel>> multipleUpdateByRequest(Map<String, Object> model, String path);

	public List<ReferencesES<TModel>> multipleUpdateByScript(Map<String, Object> model, String path,
			Boolean nestedProperty);

	public List<ReferencesES<TModel>> multipleUpdateByScript(Map<String, Object> model, String path, int nestingDepth,
			Boolean nestedProperty);

	public List<ReferencesES<TModel>> multipleDeleteByScript(String id, String path, String script,
			Boolean isNestedProperty);

	public List<ReferencesES<TModel>> multipleUpdate(List<UpdateRequest> requestBuilder, List<TModel> oldItems);
}
