package es.redmic.es.geodata.common.repository;

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
