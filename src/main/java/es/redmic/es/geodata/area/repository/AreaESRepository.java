package es.redmic.es.geodata.area.repository;

import java.util.HashMap;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.queryFactory.geodata.AreaQueryUtils;
import es.redmic.es.geodata.common.repository.GeoDataESRepository;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.request.dto.CategoryPathInfo;
import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.models.es.geojson.common.model.GeoMultiPolygonData;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;

@Repository
public class AreaESRepository extends GeoDataESRepository<GeoMultiPolygonData> {

	@Value("${controller.mapping.AREA_TYPE}")
	private String AREA_TYPE_TARGET;

	public AreaESRepository() {
		super();
		setInternalQuery(AreaQueryUtils.INTERNAL_QUERY);
	}

	public GeoHitWrapper<?, ?> findBySamplingId(String samplingId) {

		QueryBuilder termQuery = QueryBuilders.termQuery("properties.samplingPlace.id", samplingId),
				queryBuilder = getQuery(new DataQueryDTO(), getInternalQuery(), termQuery);

		GeoSearchWrapper<?, ?> result = findBy(QueryBuilders.boolQuery().must(queryBuilder));

		if (result == null || result.getTotal() != 1) {
			throw new ItemNotFoundException("id", samplingId);
		}

		return result.getHits().getHits().get(0);
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "properties.samplingPlace.name", "properties.samplingPlace.name.suggest",
				"properties.samplingPlace.code", "properties.samplingPlace.code.suggest",
				"properties.samplingPlace.remarks", "properties.samplingPlace.remarks.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "properties.samplingPlace.name.suggest", "properties.samplingPlace.code.suggest",
				"properties.samplingPlace.remarks.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "properties.samplingPlace.name", "properties.samplingPlace.code",
				"properties.samplingPlace.remarks" };
	}

	@Override
	public HashMap<String, CategoryPathInfo> getCategoriesPaths() {

		// TODO: usar el diccionario de dto a model cuando esté implementado
		HashMap<String, CategoryPathInfo> categoriesPaths = new HashMap<>();
		categoriesPaths.put("properties.areaType.id",
				new CategoryPathInfo("properties.samplingPlace.areaType.id", AREA_TYPE_TARGET));
		return categoriesPaths;
	}
}