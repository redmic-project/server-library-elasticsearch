package es.redmic.es.atlas.repository;

import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.HierarchicalESRepository;
import es.redmic.models.es.atlas.dto.LayerDTO;
import es.redmic.models.es.atlas.model.LayerModel;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Repository
public class LayerESRepository extends HierarchicalESRepository<LayerModel, LayerDTO> {

	private static String[] INDEX = { "atlas" };
	private static String[] TYPE = { "layer" };

	public LayerESRepository() {
		super(INDEX, TYPE);
	}

	@Override
	public DataSearchWrapper<?> findByAscendants(List<String> pathParent) {

		QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();

		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.termsQuery("path.split", pathParent))
				.must(QueryBuilders.existsQuery("urlSource"));

		return findBy(QueryBuilders.boolQuery().must(queryBuilder).filter(filterBuilder));
	}

	/**
	 * Sobrescribe a la función base por incompatibilidad de query. Función que dado un
	 * conjunto de términos, nos devuelve una query de elasticsearch. Debe estar
	 * implementado en cada repositorio para darle una funcionalidad específica y aquí
	 * estarán las funcionalidades que comparten todos los repositorios.
	 * 
	 * @param terms Map de términos pasados por la query.
	 * @param query QueryBuilder con la query de los términos acumulados en los
	 * repositorios específicos.
	 * @return query de tipo terms de elasticsearch.
	 */
	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("urlSource") && terms.containsValue("NOT_NULL")) {
			query.must(QueryBuilders.boolQuery()
					.must(QueryBuilders.existsQuery("urlSource")));
		}
		if (terms.containsKey("atlas")) {
			query.must(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("atlas",
					Boolean.valueOf(terms.get("atlas").toString()))));
		}
		return super.getTermQuery(terms, query);
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "title", "title.suggest", "alias", "alias.suggest",
				"keyword", "keyword.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "title.suggest", "alias.suggest", "keyword.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "title", "alias", "keyword" };
	}
}
