package es.redmic.es.series.common.repository;

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
