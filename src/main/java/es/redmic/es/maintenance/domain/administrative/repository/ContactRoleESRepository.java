package es.redmic.es.maintenance.domain.administrative.repository;

import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.common.model.DomainES;

@Repository
public class ContactRoleESRepository extends DomainESRepository<DomainES> {

	private static String[] INDEX = { "administrative-domains" };
	private static String[] TYPE = { "contactrole" };

	public ContactRoleESRepository() {
		super(INDEX, TYPE);
	}

	@Override
	/*
	 * Función que sobrescribe a getTermQuery de RElasticSearchRepository para
	 * añadir implementación específica para crear una query a apartir de una
	 * serie de términos obtenidos por el controlador.
	 */
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {
		String ids = (String) terms.get("ids");
		if (terms.containsKey("ids")) {
			query.filter(QueryBuilders.termsQuery("id", ids.split(",")));
		}
		return super.getTermQuery(terms, query);
	}
}
