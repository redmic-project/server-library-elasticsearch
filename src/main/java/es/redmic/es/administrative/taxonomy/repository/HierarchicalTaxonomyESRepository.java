package es.redmic.es.administrative.taxonomy.repository;

import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.es.common.repository.HierarchicalESRepository;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonWithOutParentDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public abstract class HierarchicalTaxonomyESRepository<TModel extends Taxon, TDTO extends TaxonWithOutParentDTO>
		extends HierarchicalESRepository<TModel, TDTO> {

	private static String[] INDEX = { "taxons" };
	private static String[] TYPE = { "taxon" };

	public HierarchicalTaxonomyESRepository() {
		super(INDEX, TYPE);
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Taxon> findByAphia(Integer aphia) {

		return (DataSearchWrapper<Taxon>) findBy(QueryBuilders.boolQuery()
				.filter(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("aphia", aphia))));
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Taxon> findByScientificNameRankStatusAndParent(String scientificName, String rank,
			String status, Long parentId) {

		return (DataSearchWrapper<Taxon>) findBy(QueryBuilders.boolQuery()
				.filter(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("scientificName", scientificName))
						.must(QueryBuilders.termQuery("rank.name_en", rank))
						.must(QueryBuilders.termQuery("status.name_en", status))
						.must(QueryBuilders.termQuery("path.split", parentId))));
	}
	
	@SuppressWarnings("unchecked")
	public DataSearchWrapper<Taxon> findByScientificNameRankAndStatus(String scientificName, String rank,
			String status) {

		return (DataSearchWrapper<Taxon>) findBy(QueryBuilders.boolQuery()
				.filter(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("scientificName", scientificName))
						.must(QueryBuilders.termQuery("rank.name_en", rank))
						.must(QueryBuilders.termQuery("status.name_en", status))));
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "scientificName", "aphia", "scientificName.suggest", "commonName", "commonName.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "scientificName", "commonName", "scientificName.suggest", "commonName.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "scientificName", "aphia", "commonName" };
	}

	@Override
	/**
	 * Sobrescribe a la función base para ampliar la query. Función que dado un
	 * conjunto de términos, nos devuelve una query de elasticsearch. Debe estar
	 * implementado en cada repositorio para darle una funcionalidad específica
	 * y aquí estarán las funcionalidades que comparten todos los repositorios.
	 * 
	 * @param terms
	 *            Map de términos pasados por la query.
	 * @param query
	 *            QueryBuilder con la query de los términos acumulados en los
	 *            repositorios específicos.
	 * @return query de tipo terms de elasticsearch.
	 */

	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("aphia") && terms.get("aphia").equals("isNull")) {

			query.mustNot(QueryBuilders.boolQuery().filter(QueryBuilders.existsQuery("aphia")));
		}
		return super.getTermQuery(terms, query);
	}
}
