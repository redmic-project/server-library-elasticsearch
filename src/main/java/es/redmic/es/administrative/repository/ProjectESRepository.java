package es.redmic.es.administrative.repository;

import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.service.UserUtilsServiceItfc;
import es.redmic.models.es.administrative.model.Project;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Repository
public class ProjectESRepository extends ActivityCommonESRepository<Project> {

	private static String[] INDEX = { "activity" };
	private static String[] TYPE = { "project" };

	@Autowired
	UserUtilsServiceItfc userService;

	public ProjectESRepository() {
		super(INDEX, TYPE);
	}

	/**
	 * Función que sobrescribe a getTermQuery de RElasticSearchRepository para
	 * añadir implementación específica para crear una query a apartir de una
	 * serie de términos obtenidos por el controlador.
	 */
	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("path.split")) {
			query.must(QueryBuilders.termsQuery("path.split", terms.get("path.split")));
		}
		return super.getTermQuery(terms, query);
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Project> findByParent(String programId) {

		QueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("path.split", programId));

		return (DataSearchWrapper<Project>) findBy(QueryBuilders.boolQuery().must(query));
	}
}
