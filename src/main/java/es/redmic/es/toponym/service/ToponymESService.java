package es.redmic.es.toponym.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.geodata.common.repository.RWGeoDataESRepository;
import es.redmic.es.geodata.common.service.RWGeoDataESService;
import es.redmic.es.toponym.repository.ToponymESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.geojson.toponym.dto.ToponymDTO;
import es.redmic.models.es.geojson.toponym.model.Toponym;

@Service
public class ToponymESService extends RWGeoDataESService<ToponymDTO, Toponym> {
	
	@Autowired
	public ToponymESService(ToponymESRepository repository) {
		super((RWGeoDataESRepository<Toponym>) repository);
	}
	
	/**
	 * Función para rellenar el modelo de elastic a apartir del dto.
	 *
	 * @param dtoToIndex
	 *            Dto con la información a guardar.
	 *
	 * @return Modelo que se va a indexar en elastic.
	 */
	
	public Toponym mapper(ToponymDTO dtoToIndex) {
		
		return orikaMapper.getMapperFacade().map(dtoToIndex, Toponym.class);
	}

	/*
	 * Función que debe estar definida en los servicios específicos.
	 * Se usa para realizar acciones en otros servicios después de modificar.
	 */
	protected void postUpdate(ReferencesES<Toponym> reference) {
	}

	
	protected void postSave(Object model) {
	}

	/*
	 * Función que debe estar definida en los servicios para tener el item antes de borrarlo.
	 */
	protected void preDelete(Object model) {
	}

	/*
	 * Función que debe estar definida en los servicios específicos.
	 * Se usa para realizar acciones en otros servicios después de borrar.
	 */
	protected void postDelete(String id) {}
	
	public void updateToponymType(ReferencesES<DomainES> reference) {
		// TODO: Llamar a updateReference
	}
}