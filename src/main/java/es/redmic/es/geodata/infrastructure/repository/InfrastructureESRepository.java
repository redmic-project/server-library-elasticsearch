package es.redmic.es.geodata.infrastructure.repository;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.queryFactory.geodata.InfrastructureQueryUtils;
import es.redmic.es.geodata.common.repository.GeoDataESRepository;
import es.redmic.models.es.common.request.dto.CategoryPathInfo;
import es.redmic.models.es.geojson.common.model.GeoPointData;

@Repository
public class InfrastructureESRepository extends GeoDataESRepository<GeoPointData> {

	protected final static String BASE_PATH = "properties.site";
	
	@Value("${controller.mapping.INFRASTRUCTURE_TYPE}")
	private String INFRASTRUCTURE_TYPE_TARGET;

	public InfrastructureESRepository() {
		super();
		setInternalQuery(InfrastructureQueryUtils.INTERNAL_QUERY);
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { 
				"properties.site.name.suggest",
				"properties.site.name",
				"properties.site.code.suggest",
				"properties.site.code",
				"properties.site.remark"
				};
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "properties.site.name.suggest", "properties.site.code.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] {
				"properties.site.name",
				"properties.site.code"
				};
	}

	@Override
	public HashMap<String, CategoryPathInfo> getCategoriesPaths() {
		
		// TODO: usar el diccionario de dto a model cuando esté implementado
		HashMap<String, CategoryPathInfo> categoriesPaths = new HashMap<>();
		categoriesPaths.put("properties.infrastructureType.id", new CategoryPathInfo("properties.site.infrastructureType.id", INFRASTRUCTURE_TYPE_TARGET));
		
		return categoriesPaths;
	}
}