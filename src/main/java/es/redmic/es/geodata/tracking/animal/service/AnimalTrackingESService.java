package es.redmic.es.geodata.tracking.animal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.Point;

import es.redmic.es.geodata.common.service.GeoPresenceESService;
import es.redmic.es.geodata.tracking.animal.repository.AnimalTrackingESRepository;
import es.redmic.es.tools.distributions.species.service.RWTaxonDistributionService;
import es.redmic.models.es.administrative.taxonomy.model.Animal;
import es.redmic.models.es.administrative.taxonomy.model.AnimalCompact;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.tracking.animal.dto.AnimalTrackingDTO;
import es.redmic.models.es.maintenance.device.model.Device;
import es.redmic.models.es.maintenance.device.model.DeviceCompact;

@Service
public class AnimalTrackingESService extends GeoPresenceESService<AnimalTrackingDTO, GeoPointData> {
	
	@Autowired
	@Qualifier("RWTaxonDistributionService")
	RWTaxonDistributionService taxonDistributionService;

	AnimalTrackingESRepository repository;
	
	/*Índica si las referencias a actualizar son de tipo anidadas*/
	private static Boolean isNestedProperty = false;
	
	/*Path de elastic para buscar por animal*/
	private String animalPropertyPath = "properties.collect.animal.id";
	/*Clase del modelo indexado en la referencia*/
	private static Class<AnimalCompact> animalClassInReference = AnimalCompact.class;
	
	/*Path de elastic para buscar por animal*/
	private String devicePropertyPath = "properties.inTrack.device.id";
	/*Clase del modelo indexado en la referencia*/
	private static Class<DeviceCompact> deviceClassInReference = DeviceCompact.class;

	@Autowired
	public AnimalTrackingESService(AnimalTrackingESRepository repository) {
		super(repository);
		this.repository = repository;
	}
	
	/**
	 * Función para rellenar el modelo de elastic a apartir del dto.
	 *
	 * @param dtoToIndex
	 *            Dto con la información a guardar.
	 *
	 * @return Modelo que se va a indexar en elastic.
	 */
	
	@Override
	public GeoPointData mapper(AnimalTrackingDTO dtoToIndex) {
		
		return orikaMapper.getMapperFacade().map(dtoToIndex, GeoPointData.class);
	}
	
	/**
	 * Función para modificar las referencias de animal en animalTracking en caso de ser necesario.
	 * 
	 * @param reference clase que encapsula el modelo de animal antes y después de ser modificado.
	 */

	public void updateAnimal(ReferencesES<Animal> reference) {
		
		updateReferenceByScript(reference, animalClassInReference, animalPropertyPath, isNestedProperty);
	}
	
	/**
	 * Función para modificar las referencias de device en animalTracking en caso de ser necesario.
	 * 
	 * @param reference clase que encapsula el modelo de device antes y después de ser modificado.
	 */

	public void updateDevice(ReferencesES<Device> reference) {
		
		updateReferenceByScript(reference, deviceClassInReference, devicePropertyPath, isNestedProperty);
	}

	/*
	 * Función que debe estar definida en los servicios específicos.
	 * Se usa para realizar acciones en otros servicios después de modificar.
	 */
	
	@Override
	protected void postUpdate(ReferencesES<GeoPointData> reference) {
		taxonDistributionService.updateAnimalTracking(reference);
	}

	/*
	 *  Función que debe estar definida en los servicios específicos. 
	 *  Se usa para realizar acciones en otros servicios después de añadir.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void postSave(Object model) {
		taxonDistributionService.addAnimalTracking((Feature<GeoDataProperties, Point>) model);
	}

	/*
	 * Función que debe estar definida en los servicios para tener el item antes de borrarlo.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void preDelete(Object model) {
		taxonDistributionService.deleteAnimalTracking((Feature<GeoDataProperties, Point>) model);
	}

	
	/*
	 * Función que debe estar definida en los servicios específicos.
	 * Se usa para realizar acciones en otros servicios después de borrar.
	 */
	@Override
	protected void postDelete(String id) {}
}
