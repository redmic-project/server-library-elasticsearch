package es.redmic.es.common.utils;

import java.io.IOException;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.config.EsClientProvider;
import es.redmic.exception.elasticsearch.ESIndexException;
import es.redmic.exception.elasticsearch.ESUpdateException;
import es.redmic.models.es.common.model.BaseES;

@Component
public class ElasticPersistenceUtils<TModel extends BaseES<?>> {

	private static final String SCRIPT_LANG = "painless";

	protected static Logger logger = LogManager.getLogger();

	@Autowired
	EsClientProvider ESProvider;

	@Autowired
	protected ObjectMapper objectMapper;

	public void save(String index, String type, Map<String, Object> source) {

		IndexRequest request = new IndexRequest(index, type)
			.source(source).setRefreshPolicy(RefreshPolicy.IMMEDIATE);

		try {
			ESProvider.getClient().index(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			logger.debug("Error indexando en {} {}",index, type);
			e.printStackTrace();
			throw new ESIndexException();
		}
	}

	public TModel save(String index, String type, TModel model, String id) {
		return save(index, type, model, id, null);
	}

	public TModel save(String index, String type, TModel model, String id, String parentId) {

		// @formatter:off

		IndexRequest request = new IndexRequest(index, type)
				.source(convertTModelToSource(model))
				.id(id)
				.setRefreshPolicy(RefreshPolicy.IMMEDIATE);

		if (parentId != null) {
			request.routing(parentId);
		}

		// @formatter:on

		try {
			ESProvider.getClient().index(request, RequestOptions.DEFAULT);
			return model;
		} catch (IOException e) {
			logger.debug("Error indexando en {} {}",index, type);
			e.printStackTrace();
			throw new ESIndexException();
		}
	}

	public IndexRequest getIndexRequest(String index, String type, TModel modelToIndex, String id) {
		return getIndexRequest(index, type, modelToIndex, id, null);
	}

	public IndexRequest getIndexRequest(String index, String type, TModel modelToIndex, String id, String parentId) {

		IndexRequest indexRequest = new IndexRequest();
		indexRequest.index(index);
		indexRequest.type(type);
		indexRequest.source(convertTModelToSource(modelToIndex));
		indexRequest.id(id);

		if (parentId != null) {
			indexRequest.parent(parentId);
		}

		return indexRequest;
	}

	public TModel update(String index, String type, TModel model, String id) {
		return update(index, type, model, id, null, null);
	}

	public TModel update(String index, String type, TModel model, String id, Class<TModel> typeOfTModel) {
		return update(index, type, model, id, null, typeOfTModel);
	}

	public TModel update(String index, String type, TModel model, String id, String parentId) {
		return update(index, type, model, id, parentId, null);
	}

	public TModel update(String index, String type, TModel model, String id, String parentId, Class<TModel> typeOfTModel) {

		// @formatter:off

		UpdateRequest updateRequest = new UpdateRequest(index, type, id)
				.doc(convertTModelToSource(model))
				.fetchSource(false)
				.setRefreshPolicy(RefreshPolicy.IMMEDIATE);

		if (parentId != null) {
			updateRequest.routing(parentId);
		}

		// @formatter:on

		try {
			UpdateResponse result = ESProvider.getClient().update(updateRequest, RequestOptions.DEFAULT);

			if (typeOfTModel != null)
				return objectMapper.convertValue(result.getGetResult().getSource(), typeOfTModel);
			return model;
		} catch (IOException e) {
			logger.debug("Error modificando el item con id {} en {} {}",id, index, type);
			e.printStackTrace();
			throw new ESUpdateException(e);
		}

	}

	public TModel update(String index, String type, String id, XContentBuilder doc, Class<TModel> typeOfTModel) {

		return update(index, type, id, null, doc, typeOfTModel);
	}

	public TModel update(String index, String type, String id, String parentId, XContentBuilder doc, Class<TModel> typeOfTModel) {

		// @formatter:off

		UpdateRequest updateRequest = new UpdateRequest(index, type, id)
				.setRefreshPolicy(RefreshPolicy.IMMEDIATE)
				.doc(doc)
				.fetchSource(true);

		// @formatter:on

		if (parentId != null) {
			updateRequest.routing(parentId);
		}

		try {
			UpdateResponse result = ESProvider.getClient().update(updateRequest, RequestOptions.DEFAULT);
			return objectMapper.convertValue(result.getGetResult().getSource(), typeOfTModel);
		} catch (Exception e) {
			logger.debug("Error modificando el item con id {} en {} {}", id, index, type);
			e.printStackTrace();
			throw new ESUpdateException(e);
		}
	}

	public Boolean delete(String index, String type, String id) {

		return delete(index, type, id, null);
	}

	public Boolean delete(String index, String type, String id, String parentId) {

		DeleteRequest deleteRequest = new DeleteRequest(index, type, id).setRefreshPolicy(RefreshPolicy.IMMEDIATE);

		if (parentId != null) {
			deleteRequest.routing(parentId);
		}

		try {
			return ESProvider.getClient().delete(deleteRequest, RequestOptions.DEFAULT).status().equals(RestStatus.OK);
		} catch (IOException e) {
			logger.debug("Error borrando el item con id {} en {} {}", id, index, type);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	protected <TModel> Map<String, Object> convertTModelToSource(TModel modelToIndex) {
		return objectMapper.convertValue(modelToIndex, Map.class);
	}

	public List<UpdateRequest> getUpdateRequest(String[] index, String type, String id, Map<String, Object> fields) {

		return getUpdateRequest(index, type, id, fields, null, null);
	}

	public List<UpdateRequest> getUpdateRequest(String[] index, String type, String id, Map<String, Object> fields,
			String parentId) {

		return getUpdateRequest(index, type, id, fields, parentId, null);
	}

	public List<UpdateRequest> getUpdateRequest(String[] index, String type, String id, Map<String, Object> fields,
			String parentId, String grandParentId) {

		List<UpdateRequest> result = new ArrayList<>();

		for (int i = 0; i < index.length; i++) {
			UpdateRequest updateRequest = new UpdateRequest();
			updateRequest.index(index[i]);
			updateRequest.type(type);
			updateRequest.id(id);
			updateRequest.fetchSource(true);
			if (parentId != null)
				updateRequest.parent(grandParentId);

			if (grandParentId != null)
				updateRequest.routing(grandParentId);

			updateRequest.doc(fields);
			result.add(updateRequest);
		}
		return result;
	}

	public List<UpdateRequest> getUpdateScript(String[] index, String type, String id, Map<String, Object> fields,
			String scriptNameOrCode) {

		return getUpdateScript(index, type, id, fields, scriptNameOrCode, null, null, false);
	}

	public List<UpdateRequest> getUpdateScript(String[] index, String type, String id, Map<String, Object> fields,
			String scriptNameOrCode, Boolean inline) {

		return getUpdateScript(index, type, id, fields, scriptNameOrCode, null, null, inline);
	}

	public List<UpdateRequest> getUpdateScript(String[] index, String type, String id, Map<String, Object> fields,
			String scriptNameOrCode, String parentId, Boolean inline) {

		return getUpdateScript(index, type, id, fields, scriptNameOrCode, parentId, null, inline);
	}

	public List<UpdateRequest> getUpdateScript(String[] index, String type, String id, Map<String, Object> fields,
			String scriptNameOrCode, String parentId, String grandParentId, Boolean inline) {

		List<UpdateRequest> result = new ArrayList<>();

		for (int i = 0; i < index.length; i++) {

			UpdateRequest updateRequest = new UpdateRequest();
			updateRequest.index(index[i]);
			updateRequest.type(type);
			updateRequest.id(id);
			updateRequest.retryOnConflict(3);
			updateRequest.fetchSource(true);

			if (parentId != null)
				updateRequest.parent(parentId);

			if (grandParentId != null)
				updateRequest.routing(grandParentId);

			ScriptType scriptType;
			String lang = null;

			if (Boolean.TRUE.equals(inline)) {
				scriptType = ScriptType.INLINE;
				lang = SCRIPT_LANG;
			}
			else {
				scriptType = ScriptType.STORED;
			}

			updateRequest.script(new Script(scriptType, lang, scriptNameOrCode, fields));
			updateRequest.retryOnConflict(2);
			result.add(updateRequest);
		}
		return result;
	}

	public List<UpdateResponse> updateByBulk(List<UpdateRequest> listUpdates) {

		BulkRequest bulkRequest = new BulkRequest();

		for (int i = 0; i < listUpdates.size(); i++)
			bulkRequest.add(listUpdates.get(i));
		bulkRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);

		BulkResponse bulkResponse;
		try {
			bulkResponse = ESProvider.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESUpdateException("Error ejecutando modificación en batch");
		}

		if (bulkResponse.hasFailures()) {

			String message = bulkResponse.buildFailureMessage();
			// TODO: Cambiar si no cambia estructura de document. Descarta error
			// controlado en document
			if (!message.contains("DocumentMissingException"))
				throw new ESUpdateException(message);
		}

		BulkItemResponse[] items = bulkResponse.getItems();
		List<UpdateResponse> result = new ArrayList<>();
		for (int i = 0; i < items.length; i++) {
			UpdateResponse item = (UpdateResponse) items[i].getResponse();

			if (item != null && item.getGetResult().isExists())
				result.add(item);
		}
		return result;
	}

	public List<IndexResponse> indexByBulk(List<IndexRequest> listIndexs) {

		BulkRequest bulkRequest = new BulkRequest();

		for (int i = 0; i < listIndexs.size(); i++)
			bulkRequest.add(listIndexs.get(i));
		bulkRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);

		BulkResponse bulkResponse;
		try {
			bulkResponse = ESProvider.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESUpdateException("Error ejecutando indexación en batch");
		}

		if (bulkResponse.hasFailures())
			throw new ESUpdateException(bulkResponse.buildFailureMessage());

		BulkItemResponse[] items = bulkResponse.getItems();
		List<IndexResponse> result = new ArrayList<>();
		for (int i = 0; i < items.length; i++) {
			IndexResponse item = items[i].getResponse();
			result.add(item);
		}
		return result;
	}
}
