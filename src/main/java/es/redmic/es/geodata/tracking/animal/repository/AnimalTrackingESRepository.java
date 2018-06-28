package es.redmic.es.geodata.tracking.animal.repository;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.queryFactory.geodata.AnimalTrackingQueryUtils;
import es.redmic.es.geodata.tracking.common.repository.ClusterTrackingESRepository;
import es.redmic.models.es.common.request.dto.CategoryPathInfo;
import es.redmic.models.es.geojson.common.model.GeoPointData;

@Repository
public class AnimalTrackingESRepository extends ClusterTrackingESRepository<GeoPointData> {

	@Value("${controller.mapping.TAXONS}")
	private String TAXONS_TARGET;
	
	@Value("${controller.mapping.ANIMAL}")
	private String ANIMAL_TARGET;
	
	@Value("${controller.mapping.DEVICE}")
	private String DEVICE_TARGET;

	public AnimalTrackingESRepository() {
		super();
		setInternalQuery(AnimalTrackingQueryUtils.INTERNAL_QUERY);
	}

	@Override
	public HashMap<String, CategoryPathInfo> getCategoriesPaths() {
		
		// TODO: usar el diccionario de dto a model cuando esté implementado
		HashMap<String, CategoryPathInfo> categoriesPaths = new HashMap<>();
		categoriesPaths.put("properties.taxon.id", new CategoryPathInfo("properties.collect.taxon.path", TAXONS_TARGET));
		categoriesPaths.put("properties.animal.id", new CategoryPathInfo("properties.collect.animal.id", ANIMAL_TARGET));
		categoriesPaths.put("properties.device.id", new CategoryPathInfo("properties.inTrack.device.id", DEVICE_TARGET));

		return categoriesPaths;
	}
}