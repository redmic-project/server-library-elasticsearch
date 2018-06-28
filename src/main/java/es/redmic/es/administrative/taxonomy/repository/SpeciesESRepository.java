package es.redmic.es.administrative.taxonomy.repository;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.models.es.administrative.taxonomy.dto.SpeciesDTO;
import es.redmic.models.es.administrative.taxonomy.model.Species;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Repository
public class SpeciesESRepository extends HierarchicalTaxonomyESRepository<Species, SpeciesDTO> {

	private static QueryBuilder INTERNAL_QUERY = QueryBuilders.boolQuery()
			.must(QueryBuilders.rangeQuery("rank.id").gte(10));

	public SpeciesESRepository() {
		super();
	}

	@Override
	public QueryBuilder getInternalQuery() {
		return INTERNAL_QUERY;
	}

	@Override
	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Species> findByAscendants(List<String> pathParent) {

		QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();

		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.termsQuery("path.split", pathParent))
				.must(QueryBuilders.rangeQuery("rank.id").gte(10)).must(QueryBuilders.termQuery("leaves", 0));

		return (DataSearchWrapper<Species>) findBy(QueryBuilders.boolQuery().must(queryBuilder).filter(filterBuilder));
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "scientificName", "commonName", "peculiarity.popularNames", "scientificName.suggest",
				"commonName.suggest", "peculiarity.popularNames.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "scientificName", "commonName", "peculiarity.popularNames", "scientificName.suggest",
				"commonName.suggest", "peculiarity.popularNames.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "scientificName", "aphia", "commonName", "peculiarity.popularNames" };
	}
}
