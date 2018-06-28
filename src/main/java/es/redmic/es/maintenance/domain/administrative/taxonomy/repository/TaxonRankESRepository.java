package es.redmic.es.maintenance.domain.administrative.taxonomy.repository;

import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.common.model.DomainES;

@Repository
public class TaxonRankESRepository extends DomainESRepository<DomainES> {
	private static String[] INDEX = { "taxon-domains" };
	private static String[] TYPE = { "rank" };

	public TaxonRankESRepository() {
		super(INDEX, TYPE);
	}

	@Override
	/*
	 * Función que sobrescribe a getTermQuery de RElasticSearchRepository para
	 * añadir implementación específica para crear una query a apartir de una
	 * serie de términos obtenidos por el controlador.
	 */
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("id")) {
			query.filter(QueryBuilders.rangeQuery("id").gte(terms.get("id")));
		}
		return super.getTermQuery(terms, query);
	}
}
