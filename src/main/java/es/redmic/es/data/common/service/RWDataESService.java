package es.redmic.es.data.common.service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import es.redmic.es.common.repository.DataSessionRepository;
import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.common.dto.BaseDTO;
import es.redmic.models.es.common.dto.DTOEvent;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.ReferencesES;

public abstract class RWDataESService<TModel extends BaseES<?>, TDTO extends BaseDTO<?>>
		extends RDataESService<TModel, TDTO> implements ApplicationListener<DTOEvent>, IRWDataESService<TModel> {

	private RWDataESRepository<TModel> repository;

	protected static String DELETE_EVENT = "DELETE";
	protected static String ADD_EVENT = "ADD";
	protected static String UPDATE_EVENT = "UPDATE";

	@Autowired(required = false)
	DataSessionRepository<TModel> dataSessionRepository;

	public RWDataESService(RWDataESRepository<TModel> repository) {
		super(repository);
		this.repository = repository;
	}

	@Override
	public TModel save(TModel modelToIndex) {

		TModel modelResult = repository.save(modelToIndex);
		transactSave(modelToIndex, modelResult);
		return modelResult;
	}

	public List<TModel> save(List<TModel> modelToIndexList) {

		List<TModel> resultingModels = repository.save(modelToIndexList);

		for (TModel modelResult : resultingModels) {

			for (TModel modelToIndex : modelToIndexList) {

				if (modelToIndex.getId().equals(modelResult.getId())) {
					transactSave(modelToIndex, modelResult);
					break;
				}
			}
		}
		return resultingModels;
	}

	@Override
	public TModel update(TModel modelToIndex) {

		TModel origin = findById(modelToIndex.getId().toString());
		if (origin != null) {
			TModel target = repository.update(modelToIndex);
			transactUpdate(origin, target);
			return target;
		} else
			return save(modelToIndex);

	}

	@Override
	public void delete(String id) {

		TModel item = findById(id);
		preDelete(item);
		Boolean removed = repository.delete(id);
		if (removed)
			transactDelete(item);
	}

	/**
	 * Función para modificar referencias vía update. Usado para propiedades
	 * simples.
	 * 
	 * @param reference
	 *            Clase que encapsula el modelo a indexar antes y después de ser
	 *            modificado.
	 * @param classInReference
	 *            Clase del modelo mapeado en la referencia.
	 * @param propertyPath
	 *            String para buscar en elastic la propiedad.
	 */
	@Override
	public void updateReference(ReferencesES<?> reference, Class<?> classInReference, String propertyPath) {

		reference.setClassForMapping(classInReference);
		if (reference.needUpdate()) {

			Map<String, Object> mapItem = reference.getModelToIndex();

			List<ReferencesES<TModel>> updates = repository.multipleUpdateByRequest(mapItem, propertyPath);
			transactUpdate(updates);
		}
	}

	/**
	 * Función para modificar referencias vía script para propiedades anidadas.
	 * Por defecto nestedProperty = true y nestingDepth = 1.
	 * 
	 * @param reference
	 *            Clase que encapsula el modelo a indexar antes y después de ser
	 *            modificado.
	 * @param classInReference
	 *            Clase del modelo mapeado en la referencia.
	 * @param propertyPath
	 *            String para buscar en elastic la propiedad.
	 */

	@Override
	public void updateReferenceByScript(ReferencesES<?> reference, Class<?> classInReference, String propertyPath) {

		updateReferenceByScript(reference, classInReference, propertyPath, 1, true);
	}

	/**
	 * Función para modificar referencias vía script para propiedades anidadas.
	 * Por defecto nestedProperty = true.
	 * 
	 * @param reference
	 *            Clase que encapsula el modelo a indexar antes y después de ser
	 *            modificado.
	 * @param classInReference
	 *            Clase del modelo mapeado en la referencia.
	 * @param propertyPath
	 *            String para buscar en elastic la propiedad.
	 * @param nestingDepth
	 *            indica el número de elementos a eliminar del path del term
	 *            para obtener el path del objecto nested para hacer la
	 *            consulta.
	 */

	@Override
	public void updateReferenceByScript(ReferencesES<?> reference, Class<?> classInReference, String propertyPath,
			int nestingDepth) {

		updateReferenceByScript(reference, classInReference, propertyPath, nestingDepth, true);
	}

	/**
	 * Función para modificar referencias vía script para propiedades anidadas.
	 * Por defecto nestingDepth = 1.
	 * 
	 * @param reference
	 *            Clase que encapsula el modelo a indexar antes y después de ser
	 *            modificado.
	 * @param classInReference
	 *            Clase del modelo mapeado en la referencia.
	 * @param propertyPath
	 *            String para buscar en elastic la propiedad.
	 * @param nestedProperty
	 *            Indica si es una propiedad anidada o no.
	 */

	@Override
	public void updateReferenceByScript(ReferencesES<?> reference, Class<?> classInReference, String propertyPath,
			Boolean nestedProperty) {

		updateReferenceByScript(reference, classInReference, propertyPath, 1, nestedProperty);
	}

	/**
	 * Función para modificar referencias vía script para propiedades anidadas.
	 * 
	 * @param reference
	 *            Clase que encapsula el modelo a indexar antes y después de ser
	 *            modificado.
	 * @param classInReference
	 *            Clase del modelo mapeado en la referencia.
	 * @param propertyPath
	 *            String para buscar en elastic la propiedad.
	 * @param nestingDepth
	 *            indica el número de elementos a eliminar del path del term
	 *            para obtener el path del objecto nested para hacer la
	 *            consulta.
	 * @param nestedProperty
	 *            Indica si es una propiedad anidada o no.
	 */

	@Override
	public void updateReferenceByScript(ReferencesES<?> reference, Class<?> classInReference, String propertyPath,
			int nestingDepth, Boolean nestedProperty) {

		reference.setClassForMapping(classInReference);

		if (reference.needUpdate()) {

			Map<String, Object> mapItem = reference.getModelToIndex();

			List<ReferencesES<TModel>> updates = repository.multipleUpdateByScript(mapItem, propertyPath, nestingDepth,
					nestedProperty);
			transactUpdate(updates);
		}
	}

	/**
	 * Función para eliminar referencias vía script para propiedades anidadas.
	 * 
	 * @param id
	 *            identificador de la referencia a ser modificada.
	 * @param propertyPath
	 *            String para buscar en elastic la propiedad.
	 * @param deleteReferencesScript
	 *            Nombre del script a ejectuar.
	 * @param isNestedProperty
	 *            Indica si es una propiedad anidada o no.
	 */

	@Override
	public void deleteReferenceByScript(String id, String propertyPath, String deleteReferencesScript,
			Boolean isNestedProperty) {

		List<ReferencesES<TModel>> updates = repository.multipleDeleteByScript(id, propertyPath, deleteReferencesScript,
				isNestedProperty);
		transactUpdate(updates);
	}

	private void transactSave(TModel modelToIndex, TModel modelResult) {

		if (dataSessionRepository != null)
			dataSessionRepository.saveDataSession(modelToIndex, ADD_EVENT, repository.getIndex(), repository.getType());
		postSave(modelResult);
	}

	protected void transactUpdate(TModel modelToIndex, TModel modelResult) {

		ReferencesES<TModel> referencesES = new ReferencesES<TModel>(modelToIndex, modelResult);
		if (dataSessionRepository != null)
			dataSessionRepository.saveDataSession(modelToIndex, UPDATE_EVENT, repository.getIndex(),
					repository.getType());
		postUpdate(referencesES);
	}

	protected void transactUpdate(List<ReferencesES<TModel>> referencesESList) {

		if (dataSessionRepository != null)
			dataSessionRepository.saveDataSession(referencesESList, UPDATE_EVENT, repository.getIndex(),
					repository.getType());
		for (int i = 0; i < referencesESList.size(); i++)
			postUpdate(referencesESList.get(i));
	}

	protected void transactDelete(TModel model) {

		if (dataSessionRepository != null)
			dataSessionRepository.saveDataSession(model, DELETE_EVENT, repository.getIndex(), repository.getType());
		postDelete(model.getId().toString());
	}

	/**
	 * Función que transforma el dto de entrada al modelo que se va a guardar en
	 * elastic Debe estar definida en el servicio principal
	 * 
	 * Se trata de un método público para usar el mapper desde el exterior
	 * 
	 * @param dtoToIndex
	 *            Dto de entrada
	 */
	public abstract TModel mapper(TDTO dtoToIndex);

	/**
	 * Función para realizar acciones después de modificar un elemento Debe
	 * estar definida en el servicio principal
	 * 
	 * @param reference
	 *            clase que contiene entre otras cosas, el elemento antes y
	 *            después de ser modificado
	 */
	protected abstract void postUpdate(ReferencesES<TModel> reference);

	/**
	 * Función para realizar acciones después de añadir un elemento Debe estar
	 * definida en el servicio principal
	 * 
	 * @param object
	 *            Item que se va a guardar
	 */
	protected abstract void postSave(Object model);

	/**
	 * Función para realizar acciones antes de borrar un elemento Debe estar
	 * definida en el servicio principal
	 * 
	 * @param object
	 *            Item que se va a borrar
	 */
	protected abstract void preDelete(Object object);

	/**
	 * Función para realizar acciones después de borrar un elemento Debe estar
	 * definida en el servicio principal
	 * 
	 * @param id
	 *            Identificador del elemento borrado
	 */
	protected abstract void postDelete(String id);

	@Override
	@SuppressWarnings("unchecked")
	public void onApplicationEvent(DTOEvent event) {

		DTOEvent eventDto = event;

		if (chkEventIsMine(typeOfTDTO, eventDto)) {
			if (eventDto.getEventType().equals(UPDATE_EVENT))
				update(mapper((TDTO) eventDto.getDto()));
			else if (eventDto.getEventType().equals(ADD_EVENT))
				save(mapper((TDTO) eventDto.getDto()));
			else if (eventDto.getEventType().equals(DELETE_EVENT))
				delete(eventDto.getDto().getId().toString());
		}
	}

	public Boolean chkEventIsMine(Type typeOfTDTO, DTOEvent event) {
		return typeOfTDTO != null && event.getDto().getClass().equals(typeOfTDTO);
	}

}
