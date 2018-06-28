package es.redmic.es.administrative.taxonomy.repository;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.models.es.administrative.taxonomy.dto.KingdomDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;

@Repository
public class KingdomESRepository extends HierarchicalTaxonomyESRepository<Taxon, KingdomDTO> {

	private static QueryBuilder INTERNAL_QUERY = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("rank.id", 1));

	public KingdomESRepository() {

	}

	@Override
	public QueryBuilder getInternalQuery() {
		return INTERNAL_QUERY;
	}
}
