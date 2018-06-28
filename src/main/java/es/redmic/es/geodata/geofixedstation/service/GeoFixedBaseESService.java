package es.redmic.es.geodata.geofixedstation.service;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import es.redmic.es.geodata.common.service.GeoDataESService;
import es.redmic.es.geodata.geofixedstation.repository.GeoFixedBaseESRepository;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.geojson.common.dto.FixedSurveySeriesPropertiesDTO;
import es.redmic.models.es.geojson.common.dto.MetaFeatureDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.properties.model.Measurement;

public abstract class GeoFixedBaseESService<TDTO extends MetaFeatureDTO<FixedSurveySeriesPropertiesDTO, ?>, TModel extends Feature<GeoDataProperties, ?>> extends GeoDataESService<TDTO, TModel> {

	private GeoFixedBaseESRepository<TModel> repository;


	public GeoFixedBaseESService(GeoFixedBaseESRepository<TModel> repository) {
		super(repository);
		this.repository = repository;
	}

	public <T extends Geometry> GeoSearchWrapper<GeoDataProperties, T> findByDataDefinition(Long dataDefinitionId) {
		return repository.findByDataDefinition(dataDefinitionId);
	}
	
	/*
	 * Función que debe estar definida en los servicios específicos.
	 * Se usa para realizar acciones en otros servicios después de modificar.
	 */
	@Override
	protected void postUpdate(ReferencesES<TModel> reference) {
	}

	/*
	 *  Función que debe estar definida en los servicios específicos. 
	 *  Se usa para realizar acciones en otros servicios después de añadir.
	 */
	@Override
	protected void postSave(Object model) {}

	/*
	 * Función que debe estar definida en los servicios para tener el item antes de borrarlo.
	 */
	@Override
	protected void preDelete(Object object) {}

	
	/*
	 * Función que debe estar definida en los servicios específicos.
	 * Se usa para realizar acciones en otros servicios después de borrar.
	 */
	@Override
	protected void postDelete(String id) {}
	
	
	@SuppressWarnings({ "unchecked", "serial" })
	public List<String> getDescendantsIds(List<String> parentsPath) {
		
		List<String> ids = new ArrayList<String>();

		int size = parentsPath.size();
		List<String> paths = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			String idProperty = parentsPath.get(i);
			if (idProperty.split("\\.").length == 2)
				paths.add(idProperty);
			else
				ids.add(idProperty);
		}
		
		if (paths.size() > 0) {
			
			DataQueryDTO query = new DataQueryDTO();
			query.setReturnFields(new ArrayList<String>() {{
				add("id");
				add("uuid");
				add("properties");
			}});
			query.addTerm("ids", paths);
	
			GeoSearchWrapper<GeoDataProperties, Point> response = (GeoSearchWrapper<GeoDataProperties, Point>) repository.find(query);
	
			if (response != null) {
				List<Feature<GeoDataProperties, Point>> features = response.getSourceList();
				
				if (features.size() == 0)
					return parentsPath;
				
				for (int i = 0; i < features.size(); i++) {
					List<Measurement> measurements = features.get(i).getProperties().getMeasurements();
					for (int j=0; j<measurements.size(); j++)
						ids.add(measurements.get(j).getParameter().getPath());
				}
				
			}
		}
		return ids;
	}
}
