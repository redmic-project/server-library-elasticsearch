package es.redmic.es.common.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.index.IndexRequest;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.es.common.service.UserUtilsServiceItfc;
import es.redmic.es.common.utils.ElasticPersistenceUtils;
import es.redmic.es.config.EsClientProvider;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DataSession;
import es.redmic.models.es.common.model.ReferencesES;

@ConditionalOnProperty(prefix = "dataSession", name = "enabled", matchIfMissing = false)
@Repository
public class DataSessionRepository<TModel extends BaseES<?>> {

	private static String[] INDEX = { "data" };
	private static String[] TYPE = { "session" };

	@Autowired
	ElasticPersistenceUtils<TModel> elasticPersistenceUtils;

	@Autowired
	UserUtilsServiceItfc userService;

	@Autowired
	protected EsClientProvider ESProvider;

	@Autowired
	protected ObjectMapper objectMapper;

	/**
	 * Función para alamacenar el dato antes de ser modificado, añadido o borrado
	 * para el control de los mismos. Para ello se rellena una serie de metadatos
	 * que nos permite tener un control de los cambios.
	 * 
	 * @param toIndex
	 *            dato antes de ser modificado, añadido o borrado.
	 * @param action
	 *            acción que se realizó sobre el dato (UPDATED | REMOVED).
	 */

	@SuppressWarnings("unchecked")
	public void saveDataSession(TModel modelToIndex, String action, String index[], String type[]) {

		if (index == null || index.length < 1 || type == null || type.length < 1)
			return;

		String userId = userService.getUserId();

		DataSession session = new DataSession();
		session.setPath("/" + index[0] + "/" + type[0] + "/" + modelToIndex.getId());
		session.setUpdate(new DateTime());
		session.setContact(userId);
		session.setData(objectMapper.convertValue(modelToIndex, Map.class));
		session.setStatus(action);

		persist(session);
	}

	/**
	 * Función para alamacenar un listado de datos con la versión anterior a ser
	 * modificados, añadidos o borrados, para el control de los mismos. Para ello se
	 * rellena una serie de metadatos que nos permite tener un control de los
	 * cambios.
	 * 
	 * @param referencesESList
	 *            listado de referencias de datos antes de ser modificados, añadidos
	 *            o borrados.
	 * @param action
	 *            acción que se realizó sobre el dato (UPDATED | REMOVED).
	 * @param index
	 *            Índice del repositorio de los datos
	 * @param type
	 *            Tipo del repositorio de los datos
	 */

	@SuppressWarnings("unchecked")
	public void saveDataSession(List<ReferencesES<TModel>> referencesESList, String action, String[] index,
			String[] type) {

		if (index == null || index.length < 1 || type == null || type.length < 1 || referencesESList == null
				|| referencesESList.size() < 1)
			return;

		String userId = userService.getUserId();
		List<IndexRequest> listIndexes = new ArrayList<IndexRequest>();

		for (int i = 0; i < referencesESList.size(); i++) {

			DataSession session = new DataSession();
			session.setPath("/" + index[0] + "/" + type[0] + "/" + referencesESList.get(i).getOldModel().getId());
			session.setUpdate(new DateTime());
			session.setContact(userId);
			session.setData(objectMapper.convertValue(referencesESList.get(i).getOldModel(), Map.class));
			session.setStatus(action);

			IndexRequest indexRequest = new IndexRequest();
			indexRequest.index(INDEX[0]);
			indexRequest.type(TYPE[0]);
			indexRequest.source(objectMapper.convertValue(session, Map.class));
			listIndexes.add(indexRequest);
		}
		if (listIndexes.size() > 0)
			elasticPersistenceUtils.indexByBulk(listIndexes);
	}

	/**
	 * Función que hace persitentes los datos y metadatos asociados en el control de
	 * los datos.
	 * 
	 * @param session
	 *            dto a almacenar en dataSession.
	 */
	@SuppressWarnings("unchecked")
	private void persist(DataSession session) {

		ESProvider.getClient().prepareIndex(INDEX[0], TYPE[0]).setSource(objectMapper.convertValue(session, Map.class))
				.execute().actionGet();
	}
}