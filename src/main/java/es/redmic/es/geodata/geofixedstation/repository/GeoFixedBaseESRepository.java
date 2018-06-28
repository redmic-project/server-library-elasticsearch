package es.redmic.es.geodata.geofixedstation.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Value;

import com.vividsolutions.jts.geom.Geometry;

import es.redmic.es.geodata.common.repository.GeoDataESRepository;
import es.redmic.models.es.common.request.dto.CategoryPathInfo;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;

public abstract class GeoFixedBaseESRepository<TModel extends Feature<GeoDataProperties, ?>>
		extends GeoDataESRepository<TModel> {

	@Value("${controller.mapping.DEVICE}")
	private String DEVICE_TARGET;

	@Value("${controller.mapping.PARAMETER}")
	private String PARAMETER_TARGET;

	@Value("${controller.mapping.UNIT}")
	private String UNIT_TARGET;

	public GeoFixedBaseESRepository() {
		super();
	}

	@SuppressWarnings("unchecked")
	public <T extends Geometry> GeoSearchWrapper<GeoDataProperties, T> findByDataDefinition(Long dataDefinitionId) {

		NestedQueryBuilder filterBuilder = QueryBuilders.nestedQuery("properties.measurements",
				QueryBuilders.termQuery("properties.measurements.dataDefinition.id", dataDefinitionId), ScoreMode.Avg)
				.innerHit(new InnerHitBuilder());

		return (GeoSearchWrapper<GeoDataProperties, T>) findBy(QueryBuilders.boolQuery().filter(filterBuilder));
	}

	/*
	 * Función que sobrescribe a getTermQuery de RElasticSearchRepository para
	 * añadir implementación específica para crear una query a apartir de una
	 * serie de términos obtenidos por el controlador.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("ids")) {
			List<String> ids = (List<String>) (List<?>) terms.get("ids");
			query.must(QueryBuilders.nestedQuery("properties.measurements",
					QueryBuilders.termsQuery("properties.measurements.parameter.path.hierarchy", ids), ScoreMode.Avg));
		}
		return query;
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "properties.site.name.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "properties.site.name.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "properties.site.name" };
	}

	@Override
	public HashMap<String, CategoryPathInfo> getCategoriesPaths() {

		// TODO: usar el diccionario de dto a model cuando esté implementado
		HashMap<String, CategoryPathInfo> categoriesPaths = new HashMap<>();
		categoriesPaths.put("properties.measurements.dataDefinition.device.id", new CategoryPathInfo(
				"properties.measurements.dataDefinition.device.id", "properties.measurements", DEVICE_TARGET));
		categoriesPaths.put("properties.measurements.parameter.id", new CategoryPathInfo(
				"properties.measurements.parameter.id", "properties.measurements", PARAMETER_TARGET));
		categoriesPaths.put("properties.measurements.unit.id",
				new CategoryPathInfo("properties.measurements.unit.id", "properties.measurements", UNIT_TARGET));

		return categoriesPaths;
	}
}