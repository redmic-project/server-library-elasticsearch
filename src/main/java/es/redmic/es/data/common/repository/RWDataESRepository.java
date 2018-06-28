package es.redmic.es.data.common.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;

import es.redmic.es.common.utils.ElasticPersistenceUtils;
import es.redmic.exception.elasticsearch.ESIndexException;
import es.redmic.exception.elasticsearch.ESUpdateException;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.utils.HierarchicalUtils;

public abstract class RWDataESRepository<TModel extends BaseES<?>> extends RDataESRepository<TModel>
		implements IRWDataESRepository<TModel> {

	@Autowired
	ElasticPersistenceUtils<TModel> elasticPersistenceUtils;

	public RWDataESRepository() {
		super();
	}

	public RWDataESRepository(String[] index, String[] type) {
		super(index, type);
	}

	@Override
	public TModel save(TModel modelToIndex) {

		// @formatter:off

		IndexResponse result = ESProvider.getClient().prepareIndex(getIndex()[0], getType()[0])
				.setSource(convertTModelToSource(modelToIndex))
				.setId((modelToIndex.getId() != null) ? modelToIndex.getId().toString() : null)
				.setRefreshPolicy(RefreshPolicy.IMMEDIATE).execute().actionGet();

		// @formatter:on

		if (!result.status().equals(RestStatus.CREATED)) {
			LOGGER.debug("Error indexando en " + getIndex()[0] + " " + getType()[0]);
			throw new ESIndexException();
		}
		return modelToIndex;
	}

	public List<TModel> save(List<TModel> modelToIndexList) {

		List<IndexRequest> indexRequestList = new ArrayList<IndexRequest>();

		for (TModel modelToIndex : modelToIndexList) {

			IndexRequest indexRequest = new IndexRequest();
			indexRequest.index(getIndex()[0]);
			indexRequest.type(getType()[0]);
			indexRequest.source(convertTModelToSource(modelToIndex));
			indexRequest.id((modelToIndex.getId() != null) ? modelToIndex.getId().toString() : null);
			indexRequestList.add(indexRequest);
		}

		if (indexRequestList.size() == 0)
			return null;

		elasticPersistenceUtils.indexByBulk(indexRequestList);

		return modelToIndexList;
	}

	@Override
	public TModel update(TModel modelToIndex) {

		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
		updateRequest.index(getIndex()[0]);
		updateRequest.type(getType()[0]);
		updateRequest.id(modelToIndex.getId().toString());
		updateRequest.doc(convertTModelToSource(modelToIndex));
		updateRequest.fetchSource(true);

		UpdateResponse result;
		try {
			result = ESProvider.getClient().update(updateRequest).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new ESUpdateException(e);
		}
		return objectMapper.convertValue(result.getGetResult().getSource(), typeOfTModel);
	}

	@Override
	public Boolean delete(String id) {

		// @formatter:off

		DeleteResponse result = ESProvider.getClient().prepareDelete(getIndex()[0], getType()[0], id)
				.setRefreshPolicy(RefreshPolicy.IMMEDIATE).execute().actionGet();

		// @formatter:on

		return result.status().equals(RestStatus.OK);
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

	@Override
	public List<ReferencesES<TModel>> multipleUpdateByRequest(Map<String, Object> model, String path) {

		Map<String, Object> fields = new HashMap<String, Object>();

		String[] pathSplit = path.split("\\.");

		if (pathSplit == null || pathSplit.length == 0)
			return new ArrayList<ReferencesES<TModel>>();

		fields.put(pathSplit[0], model);

		List<TModel> oldItems = findWithSpecificReference(path, Long.parseLong(model.get("id").toString()));

		if (oldItems == null || oldItems.size() <= 0)
			return new ArrayList<ReferencesES<TModel>>();

		List<UpdateRequest> requestBuilder = new ArrayList<UpdateRequest>();

		for (int i = 0; i < oldItems.size(); i++) {
			requestBuilder.addAll(elasticPersistenceUtils.getUpdateRequest(getIndex(), getType(),
					oldItems.get(i).getId().toString(), fields));
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
	 * 
	 * @param nestedProperty
	 *            Indica si se trata de una propiedad anidada
	 */

	@Override
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

	@Override
	public List<ReferencesES<TModel>> multipleUpdateByScript(Map<String, Object> model, String path, int nestingDepth,
			Boolean nestedProperty) {

		Map<String, Object> fields = new HashMap<String, Object>();
		String[] pathSplit = path.split("\\.");
		String script;
		fields.put("item", model);

		List<TModel> oldItems;
		if (!nestedProperty) {
			oldItems = findWithSpecificReference(path, Long.parseLong(model.get("id").toString()));
			fields.put("propertyPath", StringUtils.join(Arrays.copyOf(pathSplit, pathSplit.length - 1), "."));
			script = "update-property";
		} else {
			String fieldProperty = HierarchicalUtils.getAncestorPath(path, nestingDepth);
			oldItems = findWithNestedReference(fieldProperty, path, Long.parseLong(model.get("id").toString()));
			fields.put("propertyPath", StringUtils
					.join(Arrays.copyOfRange(pathSplit, (pathSplit.length - nestingDepth), pathSplit.length - 1), "."));
			fields.put("nestedPath", fieldProperty);
			script = "update-nested";
		}
		if (oldItems == null || oldItems.size() <= 0)
			return new ArrayList<ReferencesES<TModel>>();

		List<UpdateRequest> requestBuilder = new ArrayList<UpdateRequest>();

		for (int i = 0; i < oldItems.size(); i++) {
			requestBuilder.addAll(elasticPersistenceUtils.getUpdateScript(getIndex(), getType(),
					oldItems.get(i).getId().toString(), fields, script));
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

	@Override
	public List<ReferencesES<TModel>> multipleDeleteByScript(String id, String path, String script,
			Boolean isNestedProperty) {

		Map<String, Object> fields = new HashMap<String, Object>();
		fields.put("item", id);

		String fieldProperty = HierarchicalUtils.getParentPath(path);

		List<TModel> oldItems;
		if (!isNestedProperty)
			oldItems = findWithSpecificReference(path, Long.parseLong(id));
		else
			oldItems = findWithNestedReference(fieldProperty, path, Long.parseLong(id));

		if (oldItems == null || oldItems.size() <= 0)
			return new ArrayList<ReferencesES<TModel>>();

		List<UpdateRequest> requestBuilder = new ArrayList<UpdateRequest>();

		for (int i = 0; i < oldItems.size(); i++) {
			requestBuilder.addAll(elasticPersistenceUtils.getUpdateScript(getIndex(), getType(),
					oldItems.get(i).getId().toString(), fields, script));
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

	@Override
	public List<ReferencesES<TModel>> multipleUpdate(List<UpdateRequest> requestBuilder, List<TModel> oldItems) {

		if (requestBuilder.size() > 0) {
			List<ReferencesES<TModel>> results = new ArrayList<ReferencesES<TModel>>();
			List<UpdateResponse> updates = elasticPersistenceUtils.updateByBulk(requestBuilder);
			for (int i = 0; i < updates.size(); i++) {
				ReferencesES<TModel> references = new ReferencesES<TModel>(oldItems.get(i),
						objectMapper.convertValue(updates.get(i).getGetResult().getSource(), typeOfTModel));
				results.add(references);
			}
			return results;
		}
		return new ArrayList<>();
	}
}
