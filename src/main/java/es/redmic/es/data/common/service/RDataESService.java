package es.redmic.es.data.common.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.elasticsearch.index.query.QueryBuilder;

import es.redmic.es.common.service.RBaseESService;
import es.redmic.es.data.common.repository.RDataESRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.dto.AggregationsDTO;
import es.redmic.models.es.common.dto.BaseDTO;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public abstract class RDataESService<TModel extends BaseES<?>, TDTO extends BaseDTO<?>>
		extends RBaseESService<TModel, TDTO> implements IRDataESService<TModel, TDTO> {

	private RDataESRepository<TModel> repository;

	Map<Object, Object> globalProperties = new HashMap<Object, Object>();

	public RDataESService(RDataESRepository<TModel> repository) {
		super();
		this.repository = repository;
	}

	@Override
	public TDTO get(String id) {

		return orikaMapper.getMapperFacade().map(repository.findById(id), typeOfTDTO, getMappingContext());
	}

	@SuppressWarnings("unchecked")
	@Override
	public TModel findById(String id) {

		DataHitWrapper<?> hitWrapper = repository.findById(id);
		return (TModel) hitWrapper.get_source();
	}

	@Override
	public TDTO searchById(String id) {

		DataSearchWrapper<?> hitsWrapper = repository.searchByIds(new String[] { id });
		if (hitsWrapper.getTotal() == 1)
			return orikaMapper.getMapperFacade().map(hitsWrapper.getSource(0), typeOfTDTO, getMappingContext());
		else if (hitsWrapper.getTotal() > 1)
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		return null;
	}

	@Override
	public JSONCollectionDTO find(DataQueryDTO query) {

		DataSearchWrapper<?> result = repository.find(query);

		JSONCollectionDTO collection = orikaMapper.getMapperFacade().map(result.getHits(), JSONCollectionDTO.class,
				getMappingContext());
		collection.set_aggs(orikaMapper.getMapperFacade().map(result.getAggregations(), AggregationsDTO.class));
		return collection;
	}

	/**
	 * Función para obtener sugerencias.
	 * 
	 * @param queryDTO
	 *            Query que hay que aplicar filtrar las sugerencias. Además que
	 *            los campos necesarios para obtenerlas (searchFields: campos
	 *            por los que buscar, text: string introducido porel usuario,
	 *            size: número de elementos que se va a devolver.
	 * @return lista de sugerencias encontradas.
	 */

	@Override
	public List<String> suggest(DataQueryDTO queryDTO) {

		return repository.suggest(queryDTO);
	}

	/**
	 * Función para obtener sugerencias.
	 * 
	 * @param queryDTO
	 *            Query que hay que aplicar filtrar las sugerencias. Además que
	 *            los campos necesarios para obtenerlas (searchFields: campos
	 *            por los que buscar, text: string introducido porel usuario,
	 *            size: número de elementos que se va a devolver.
	 * 
	 * @param serviceQuery
	 *            Query definida en el servicio
	 * @return lista de sugerencias encontradas.
	 */

	public List<String> suggest(DataQueryDTO queryDTO, QueryBuilder serviceQuery) {

		return repository.suggest(queryDTO, serviceQuery);
	}

	@Override
	public JSONCollectionDTO mget(MgetDTO dto) {

		return orikaMapper.getMapperFacade().map(repository.mget(dto), JSONCollectionDTO.class, getMappingContext());
	}

	@SuppressWarnings({ "unchecked", "serial" })
	public List<String> getAllIds(DataQueryDTO queryDTO, String idProperty) {

		List<String> allIds = new ArrayList<String>();

		if (queryDTO.getDataType() == null)
			queryDTO.setDataType(DataPrefixType.getPrefixTypeFromClass(typeOfTDTO));

		queryDTO.setReturnFields(new ArrayList<String>() {
			{
				add(idProperty);
			}
		});

		DataSearchWrapper<?> queryResult = repository.find(queryDTO);
		List<Object> listItems = (List<Object>) queryResult.getSourceList();

		for (int i = 0; i < listItems.size(); i++) {
			TModel itemObj = (TModel) listItems.get(i);

			try {
				allIds.add(String.valueOf(PropertyUtils.getProperty(itemObj, idProperty)));
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
			}
		}
		return allIds;
	}

	public SimpleQueryDTO createSimpleQueryDTOFromTextQueryParams(String[] fields, String text, Integer from,
			Integer size) {
		return repository.createSimpleQueryDTOFromTextQueryParams(fields, text, from, size);
	}

	public SimpleQueryDTO createSimpleQueryDTOFromSuggestQueryParams(String[] fields, String text, Integer size) {
		return repository.createSimpleQueryDTOFromSuggestQueryParams(fields, text, size);
	}
}
