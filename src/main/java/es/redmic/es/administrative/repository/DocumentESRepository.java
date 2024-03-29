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
import java.util.Map;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.AdministrativeCommonESRepository;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Repository
public class DocumentESRepository extends AdministrativeCommonESRepository<Document> {

	private static String[] INDEX = { "document-es", "document-en", "document-it", "document-de", "document-fr",
			"document-pt" };
	private static String TYPE = "_doc";

	private static String BASE_INDEX = "document-";

	public DocumentESRepository() {
		super(INDEX, TYPE);
	}

	// TODO: Buscar manera de especificar el index sin tener que sobrescribir
	// todo el método
	@Override
	public Document save(Document modelToIndex) {

		String mainIndex = BASE_INDEX + modelToIndex.getLanguage();

		Document result = elasticPersistenceUtils.save(mainIndex, getType(), modelToIndex, modelToIndex.getId().toString());

		return (Document) findById(result.getId().toString()).get_source();
	}

	@Override
	public List<Document> save(List<Document> modelToIndexList) {

		List<IndexRequest> indexRequestList = new ArrayList<>();

		for (Document modelToIndex : modelToIndexList) {

			String mainIndex = BASE_INDEX + modelToIndex.getLanguage();

			IndexRequest indexRequest = elasticPersistenceUtils.getIndexRequest(
				mainIndex, getType(), modelToIndex, (modelToIndex.getId() != null) ? modelToIndex.getId().toString() : null);
			indexRequestList.add(indexRequest);
		}

		if (indexRequestList.isEmpty())
			return new ArrayList<>();

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

		return elasticPersistenceUtils.update(mainIndex, getType(), modelToIndex, modelToIndex.getId().toString(), typeOfTModel);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean delete(String id) {

		DataHitWrapper<Document> item = (DataHitWrapper<Document>) findById(id);

		String mainIndex = BASE_INDEX + item.get_source().getLanguage();

		return elasticPersistenceUtils.delete(mainIndex, getType(), id);
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Document> findByIds(DataQueryDTO dto, String[] ids) {

		QueryBuilder queryBuilder = getQuery(dto, null, null);
		if (queryBuilder == null)
			queryBuilder = QueryBuilders.matchAllQuery();

		return (DataSearchWrapper<Document>) findBy(
				QueryBuilders.boolQuery().must(queryBuilder).filter(QueryBuilders.idsQuery(getType()).addIds(ids)));
	}

	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("only_enable") && terms.get("only_enable").equals(true)) {
			query.must(QueryBuilders.termsQuery("enabled", true));
		}
		return super.getTermQuery(terms, query);
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
