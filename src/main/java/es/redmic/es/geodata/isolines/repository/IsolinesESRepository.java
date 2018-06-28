package es.redmic.es.geodata.isolines.repository;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.queryFactory.geodata.IsolinesQueryUtils;
import es.redmic.es.geodata.common.repository.GeoDataESRepository;
import es.redmic.models.es.common.request.dto.CategoryPathInfo;
import es.redmic.models.es.geojson.common.model.GeoMultiLineStringData;

@Repository
public class IsolinesESRepository extends GeoDataESRepository<GeoMultiLineStringData> {
	
	@Value("${controller.mapping.DEVICE}")
	private String DEVICE_TARGET;
	
	@Value("${controller.mapping.PARAMETER}")
	private String PARAMETER_TARGET;
	
	@Value("${controller.mapping.UNIT}")
	private String UNIT_TARGET;
	
	public IsolinesESRepository() {
		super();
		setInternalQuery(IsolinesQueryUtils.INTERNAL_QUERY);
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "properties.samplingPlace.name.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "properties.samplingPlace.name.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "properties.samplingPlace.name" };
	}

	@Override
	public HashMap<String, CategoryPathInfo> getCategoriesPaths() {
		
		//TODO: usar el diccionario de dto a model cuando esté implementado
		HashMap<String, CategoryPathInfo> categoriesPaths = new HashMap<>();
		categoriesPaths.put("properties.measurements.dataDefinition.device.id",
				new CategoryPathInfo("properties.measurements.dataDefinition.device.id", "properties.measurements", DEVICE_TARGET));
		categoriesPaths.put("properties.measurements.parameter.id",
				new CategoryPathInfo("properties.measurements.parameter.id", "properties.measurements", PARAMETER_TARGET));
		categoriesPaths.put("properties.measurements.unit.id",
				new CategoryPathInfo("properties.measurements.unit.id", "properties.measurements", UNIT_TARGET));
		
		return categoriesPaths;
	}
}
