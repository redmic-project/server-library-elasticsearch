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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;

import es.redmic.es.common.utils.ElasticPersistenceUtils;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.utils.HierarchicalUtils;
import es.redmic.models.es.geojson.common.model.Feature;

public abstract class RWGeoDataESRepository<TModel extends Feature<?, ?>> extends RGeoDataESRepository<TModel>
		implements IRWGeoDataESRepository<TModel> {

	@Autowired
	ElasticPersistenceUtils<TModel> elasticPersistenceUtils;

	protected RWGeoDataESRepository(String[] index, String type) {
		super(index, type);
	}

	public TModel save(TModel modelToIndex) {

		return elasticPersistenceUtils.save(getIndex()[0], getType(), modelToIndex, modelToIndex.getUuid(),
			modelToIndex.getProperties().getActivityId());
	}

	public List<TModel> save(List<TModel> modelToIndexList) {

		List<IndexRequest> indexRequestList = new ArrayList<>();

		for (TModel modelToIndex : modelToIndexList) {

			String parentId = modelToIndex.getProperties().getActivityId();

			IndexRequest indexRequest = elasticPersistenceUtils.getIndexRequest(
				getIndex(modelToIndex), getType(), modelToIndex, modelToIndex.getUuid(), parentId);
			indexRequestList.add(indexRequest);
		}

		if (indexRequestList.isEmpty())
			return new ArrayList<>();

		elasticPersistenceUtils.indexByBulk(indexRequestList);

		return modelToIndexList;
	}

	public TModel update(TModel modelToIndex) {

		return elasticPersistenceUtils.update(
			getIndex(modelToIndex), getType(), modelToIndex, modelToIndex.getUuid(),
				modelToIndex.getProperties().getActivityId(), typeOfTModel);
	}

	public Boolean delete(String id, String parentId) {

		return elasticPersistenceUtils.delete(getIndex()[0], getType(), id, parentId);
	}

	/**
	 * Función para modificar todas las referencias que tengan id igual al del
	 * model pasado, vía request simple.
	 *
	 * @param model
	 *            Modelo de la referencia a modificar de tipo map
	 *            <string,object>.
	 * @param path
	 *            String que indica la ruta de elastic donde está la propiedad a
	 *            buscar.
	 */

	public List<ReferencesES<TModel>> multipleUpdateByRequest(Map<String, Object> model, String path) {

		Map<String, Object> fields = new HashMap<>();

		String[] pathSplit = path.split("\\.");

		if (pathSplit == null || pathSplit.length == 0)
			return new ArrayList<>();

		fields.put(pathSplit[0], model);

		List<TModel> oldItems = findWithSpecificReference(path, Long.parseLong(model.get("id").toString()));

		if (oldItems == null || oldItems.isEmpty())
			return new ArrayList<>();

		List<UpdateRequest> requestBuilder = new ArrayList<>();

		for (int i = 0; i < oldItems.size(); i++) {
			requestBuilder.addAll(elasticPersistenceUtils.getUpdateRequest(getIndex(), getType(),
					oldItems.get(i).getUuid(), fields, oldItems.get(i).getProperties().getActivityId()));
		}

		return multipleUpdate(requestBuilder, oldItems);
	}

	/**
	 * Función para modificar todas las referencias que tengan id igual al del
	 * model pasado, vía script.
	 *
	 * @param model
	 *            Modelo de la referencia a modificar de tipo map
	 *            <string,object>.
	 * @param path
	 *            String que indica la ruta de elastic donde está la propiedad a
	 *            buscar.
	 * @param nestedProperty
	 *            Indica si se trata de una propiedad anidada
	 */

	public List<ReferencesES<TModel>> multipleUpdateByScript(Map<String, Object> model, String path,
			Boolean nestedProperty) {

		return multipleUpdateByScript(model, path, 1, nestedProperty);
	}

	/**
	 * Función para modificar todas las referencias que tengan id igual al del
	 * model pasado, vía script.
	 *
	 * @param model
	 *            Modelo de la referencia a modificar de tipo map
	 *            <string,object>.
	 * @param path
	 *            String que indica la ruta de elastic donde está la propiedad a
	 *            buscar.
	 * @param nestingDepth
	 *            indica el número de elementos a eliminar del path del term
	 *            para obtener el path del objecto nested para hacer la
	 *            consulta.
	 * @param nestedProperty
	 *            Indica si se trata de una propiedad anidada
	 */

	public List<ReferencesES<TModel>> multipleUpdateByScript(Map<String, Object> model, String path, int nestingDepth,
			Boolean nestedProperty) {

		Map<String, Object> fields = new HashMap<>();
		String[] pathSplit = path.split("\\.");
		fields.put("item", model);

		String script;

		List<TModel> oldItems;
		if (Boolean.FALSE.equals(nestedProperty)) {
			oldItems = findWithSpecificReference(path, Long.parseLong(model.get("id").toString()));
			fields.put("propertyPath", StringUtils.join(Arrays.copyOf(pathSplit, pathSplit.length - 1), "."));
			script = getUpdatePropertyScript();
		} else {
			String fieldProperty = HierarchicalUtils.getAncestorPath(path, nestingDepth);
			oldItems = findWithNestedReference(fieldProperty, path, Long.parseLong(model.get("id").toString()));
			fields.put("propertyPath", StringUtils
					.join(Arrays.copyOfRange(pathSplit, (pathSplit.length - nestingDepth), pathSplit.length - 1), "."));
			fields.put("nestedPath", fieldProperty);
			script = getUpdateNestedPropertyScript();
		}
		if (oldItems == null || oldItems.isEmpty())
			return new ArrayList<>();

		List<UpdateRequest> requestBuilder = new ArrayList<>();

		for (int i = 0; i < oldItems.size(); i++) {
			requestBuilder.addAll(elasticPersistenceUtils.getUpdateScript(getIndex(), getType(),
					oldItems.get(i).getUuid(), fields, script, oldItems.get(i).getProperties().getActivityId(), false));
		}

		return multipleUpdate(requestBuilder, oldItems);
	}

	/**
	 * Función para eliminar todas las referencias que tengan id igual al
	 * pasado, vía script.
	 *
	 * @param id
	 *            identificador de la referencia.
	 * @param path
	 *            String que indica la ruta de elastic donde está la propiedad a
	 *            buscar.
	 * @param script
	 *            String que indica el nombre del script que debe ejecutar en
	 *            groovy
	 * @param nestedProperty
	 *            Indica si se trata de una propiedad anidada
	 */

	public List<ReferencesES<TModel>> multipleDeleteByScript(String id, String path, String script,
			Boolean isNestedProperty) {

		Map<String, Object> fields = new HashMap<>();
		fields.put("item", id);
		String fieldProperty = HierarchicalUtils.getParentPath(path);

		List<TModel> oldItems;
		if (Boolean.FALSE.equals(isNestedProperty))
			oldItems = findWithSpecificReference(path, Long.parseLong(id));
		else
			oldItems = findWithNestedReference(fieldProperty, path, Long.parseLong(id));

		if (oldItems == null || oldItems.isEmpty())
			return new ArrayList<>();

		List<UpdateRequest> requestBuilder = new ArrayList<>();

		for (int i = 0; i < oldItems.size(); i++) {
			requestBuilder.addAll(elasticPersistenceUtils.getUpdateScript(getIndex(), getType(),
					oldItems.get(i).getUuid(), fields, script, oldItems.get(i).getProperties().getActivityId(), false));
		}

		return multipleUpdate(requestBuilder, oldItems);
	}

	/**
	 * Función que dado una lista de request y una lista de items originales,
	 * ejecuta los request vía bulk y retorna una lista de referencias, es
	 * decir, lista de items antes y despues de ser modificados.
	 *
	 * @param requestBuilder
	 *            Lista de request a ejecutar.
	 * @param oldItems
	 *            Lista de modelos de los items que se van a modificar.
	 * @return Lista de referencias antes y después de ser modificadas.
	 */

	public List<ReferencesES<TModel>> multipleUpdate(List<UpdateRequest> requestBuilder, List<TModel> oldItems) {

		if (!requestBuilder.isEmpty()) {
			List<ReferencesES<TModel>> results = new ArrayList<>();
			List<UpdateResponse> updates = elasticPersistenceUtils.updateByBulk(requestBuilder);
			for (int i = 0; i < updates.size(); i++) {
				ReferencesES<TModel> references = new ReferencesES<>(oldItems.get(i),
						objectMapper.convertValue(updates.get(i).getGetResult().getSource(), typeOfTModel));
				results.add(references);
			}
			return results;
		}
		return new ArrayList<>();
	}
}
