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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.utils.ElasticPersistenceUtils;
import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.exception.elasticsearch.ESUpdateException;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Repository
public class DocumentESRepository extends RWDataESRepository<Document> {

	@Autowired
	ElasticPersistenceUtils<Document> elasticPersistenceUtils;

	private static String[] INDEX = { "document-es", "document-en", "document-it", "document-de", "document-fr",
			"document-pt" };
	private static String[] TYPE = { "document" };

	private static String BASE_INDEX = "document-";

	private final Logger LOGGER = LoggerFactory.getLogger(DocumentESRepository.class);

	public DocumentESRepository() {
		super(INDEX, TYPE);
	}

	// TODO: Buscar manera de especificar el index sin tener que sobrescribir
	// todo el método
	@Override
	public Document save(Document modelToIndex) {

		String mainIndex = BASE_INDEX + modelToIndex.getLanguage();

		IndexResponse result = ESProvider.getClient().prepareIndex(mainIndex, TYPE[0])
				.setRefreshPolicy(RefreshPolicy.IMMEDIATE).setSource(convertTModelToSource(modelToIndex))
				.setId(modelToIndex.getId().toString()).execute().actionGet();

		return (Document) findById(result.getId()).get_source();
	}

	@Override
	public List<Document> save(List<Document> modelToIndexList) {

		List<IndexRequest> indexRequestList = new ArrayList<IndexRequest>();

		for (Document modelToIndex : modelToIndexList) {

			String mainIndex = BASE_INDEX + modelToIndex.getLanguage();

			IndexRequest indexRequest = new IndexRequest();
			indexRequest.index(mainIndex);
			indexRequest.type(TYPE[0]);
			indexRequest.source(convertTModelToSource(modelToIndex));
			indexRequest.id((modelToIndex.getId() != null) ? modelToIndex.getId().toString() : null);
			indexRequestList.add(indexRequest);
		}

		if (indexRequestList.size() == 0)
			return null;

		elasticPersistenceUtils.indexByBulk(indexRequestList);

		return modelToIndexList;
	}

	// TODO: Buscar manera de especificar el index sin tener que sobrescribir
	// todo el método
	@Override
	public Document update(Document modelToIndex) {

		Document origin = (Document) findById(modelToIndex.getId().toString()).get_source();

		if (origin == null)
			throw new ItemNotFoundException("id", modelToIndex.getId().toString());

		// Si se cambia el idioma se borra del índice actual y se inserta en el
		// que le corresponde según el idioma insertado
		if (!origin.getLanguage().equals(modelToIndex.getLanguage())) {
			delete(origin.getId().toString());
			return save(modelToIndex);
		}

		// Si no se ha alterado el idioma se actualiza sobre el índice actual.

		String mainIndex = BASE_INDEX + modelToIndex.getLanguage();

		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
		updateRequest.index(mainIndex);
		updateRequest.type(TYPE[0]);
		updateRequest.id(modelToIndex.getId().toString());
		updateRequest.doc(convertTModelToSource(modelToIndex));
		updateRequest.fetchSource(true);
		UpdateResponse result;
		try {
			result = ESProvider.getClient().update(updateRequest).get();
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.debug("Error al modificar un registro con updateResponse");
			throw new ESUpdateException(e);
		}
		return objectMapper.convertValue(result.getGetResult().getSource(), Document.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean delete(String id) {

		DataHitWrapper<Document> item = (DataHitWrapper<Document>) findById(id);

		String mainIndex = BASE_INDEX + item.get_source().getLanguage();

		// @formatter:off

		DeleteResponse result = ESProvider.getClient().prepareDelete(mainIndex, getType()[0], id)
				.setRefreshPolicy(RefreshPolicy.IMMEDIATE).execute().actionGet();

		// @formatter:on

		return result.status().equals(RestStatus.OK);
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Document> findByIds(DataQueryDTO dto, String[] ids) {

		QueryBuilder queryBuilder = getQuery(dto, null, null);
		if (queryBuilder == null)
			queryBuilder = QueryBuilders.matchAllQuery();

		return (DataSearchWrapper<Document>) findBy(
				QueryBuilders.boolQuery().must(queryBuilder).filter(QueryBuilders.idsQuery(TYPE).addIds(ids)));
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "title", "title.suggest", "author", "author.suggest", "code", "code.suggest",
				"keyword.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "title", "title.suggest", "code", "code.suggest", "author", "author.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "title", "author", "code", "keywords" };
	}
}
