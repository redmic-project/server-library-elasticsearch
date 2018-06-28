package es.redmic.es.series.common.service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import es.redmic.es.common.repository.DataSessionRepository;
import es.redmic.es.series.common.repository.RWSeriesESRepository;
import es.redmic.models.es.common.dto.DTOEvent;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.series.common.dto.SeriesCommonDTO;
import es.redmic.models.es.series.common.model.SeriesCommon;

public abstract class RWSeriesESService<TModel extends SeriesCommon, TDTO extends SeriesCommonDTO>
		extends RSeriesESService<TModel, TDTO> implements ApplicationListener<DTOEvent>, IRWSeriesESService<TModel> {

	private RWSeriesESRepository<TModel> repository;

	protected static String DELETE_EVENT = "DELETE";
	protected static String ADD_EVENT = "ADD";
	protected static String UPDATE_EVENT = "UPDATE";

	@Autowired(required = false)
	DataSessionRepository<TModel> dataSessionRepository;

	public RWSeriesESService(RWSeriesESRepository<TModel> repository) {
		super(repository);
		this.repository = repository;
	}

	public TModel save(TModel modelToIndex) {

		TModel modelResult = repository.save(modelToIndex);
		modelResult.set_parentId(modelToIndex.get_parentId());
		modelResult.set_grandparentId(modelToIndex.get_grandparentId());
		transactSave(modelToIndex, modelResult);
		return modelResult;
	}

	public List<TModel> save(List<TModel> modelToIndexList) {

		List<TModel> resultingModels = repository.save(modelToIndexList);

		for (TModel modelResult : resultingModels) {

			for (TModel modelToIndex : modelToIndexList) {

				if (modelToIndex.getId().equals(modelResult.getId())) {
					modelResult.set_parentId(modelToIndex.get_parentId());
					modelResult.set_grandparentId(modelToIndex.get_grandparentId());
					transactSave(modelToIndex, modelResult);
					break;
				}
			}
		}
		return resultingModels;
	}

	public TModel update(TModel modelToIndex) {

		TModel origin = findById(modelToIndex.getId().toString(), modelToIndex.get_parentId(),
				modelToIndex.get_grandparentId());
		if (origin != null) {
			TModel target = repository.update(modelToIndex);
			target.set_parentId(modelToIndex.get_parentId());
			target.set_grandparentId(modelToIndex.get_grandparentId());
			origin.set_parentId(modelToIndex.get_parentId());
			origin.set_grandparentId(modelToIndex.get_grandparentId());
			transactUpdate(origin, target);
			return target;
		} else
			return save(modelToIndex);
	}

	public void delete(String id, String parentId, String grandparentId) {

		TModel item = findById(id, parentId, grandparentId);
		preDelete(item);
		Boolean removed = repository.delete(id, parentId, grandparentId);
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
	public void updateReference(ReferencesES<?> reference, Class<?> classInReference, String propertyPath) {

		reference.setClassForMapping(classInReference);
		if (reference.needUpdate()) {

			Map<String, Object> mapItem = reference.getModelToIndex();

			List<ReferencesES<TModel>> updates = repository.multipleUpdateByRequest(mapItem, propertyPath);
			transactUpdate(updates);
		}
	}

	/**
	 * Función para modificar referencias en double nested vía script para
	 * propiedades doblemente anidadas.
	 * 
	 * @param reference
	 *            Clase que encapsula el modelo a indexar antes y después de ser
	 *            modificado.
	 * @param classInReference
	 *            Clase del modelo mapeado en la referencia.
	 * @param propertyPath
	 *            String para buscar en elastic la propiedad.
	 */

	public void updateReferenceDoubleNestedByScript(ReferencesES<?> reference, Class<?> classInReference,
			String nestedPath, String fieldPropertyPath, String script) {

		reference.setClassForMapping(classInReference);
		if (reference.needUpdate()) {

			Map<String, Object> mapItem = reference.getModelToIndex();

			List<ReferencesES<TModel>> updates = repository.multipleUpdateDoubleNestedByScript(mapItem, nestedPath,
					fieldPropertyPath, script);
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

	protected void transactSave(TModel modelToIndex, TModel modelResult) {

		if (dataSessionRepository != null)
			dataSessionRepository.saveDataSession(modelToIndex, ADD_EVENT, repository.getIndex(), repository.getType());
		postSave(modelResult);
	}

	protected void transactUpdate(TModel modelToIndex, TModel modelResult) {

		transactUpdate(new ReferencesES<TModel>(modelToIndex, modelResult));
	}

	protected void transactUpdate(List<ReferencesES<TModel>> referencesESList) {

		if (dataSessionRepository != null)
			dataSessionRepository.saveDataSession(referencesESList, UPDATE_EVENT, repository.getIndex(),
					repository.getType());
		for (int i = 0; i < referencesESList.size(); i++)
			postUpdate(referencesESList.get(i));
	}

	protected void transactUpdate(ReferencesES<TModel> referencesES) {

		if (dataSessionRepository != null)
			dataSessionRepository.saveDataSession(referencesES.getOldModel(), UPDATE_EVENT, repository.getIndex(),
					repository.getType());
		postUpdate(referencesES);
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

	@SuppressWarnings("unchecked")
	public void onApplicationEvent(DTOEvent event) {

		DTOEvent eventDto = event;

		if (chkEventIsMine(typeOfTDTO, eventDto)) {
			if (eventDto.getEventType().equals(UPDATE_EVENT))
				update(mapper((TDTO) eventDto.getDto()));
			else if (eventDto.getEventType().equals(ADD_EVENT))
				save(mapper((TDTO) eventDto.getDto()));
			else if (eventDto.getEventType().equals(DELETE_EVENT)) {
				TDTO dto = (TDTO) eventDto.getDto();
				delete(dto.getId().toString(), dto.get_parentId(), dto.get_grandparentId());
			}
		}
	}

	public Boolean chkEventIsMine(Type typeOfTDTO, DTOEvent event) {
		return typeOfTDTO != null && event.getDto().getClass().equals(typeOfTDTO);
	}

}
