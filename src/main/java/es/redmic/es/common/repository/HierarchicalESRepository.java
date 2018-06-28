package es.redmic.es.common.repository;

import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.common.dto.BaseDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public abstract class HierarchicalESRepository<TModel extends BaseES<?>, TDTO extends BaseDTO<?>>
		extends RWDataESRepository<TModel> {

	public HierarchicalESRepository() {
		super();
	}

	public HierarchicalESRepository(String[] index, String[] type) {
		super(index, type);
	}

	@SuppressWarnings("unchecked")
	public List<TModel> getChildren(String id) {

		QueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery())
				.filter(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("path.split", id)));

		DataSearchWrapper<?> result = findBy(query);

		return (List<TModel>) result.getSourceList();
	}

	public DataSearchWrapper<?> findByAscendants(List<String> pathParent) {

		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.termsQuery("path.split", pathParent));

		return findBy(QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery()).filter(filterBuilder));
	}

	public DataSearchWrapper<?> findChildByAscendants(List<String> pathParent) {

		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.termsQuery("path.split", pathParent))
				.must(QueryBuilders.rangeQuery("rank.id").from(10).to(12));

		return findBy(QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery()).filter(filterBuilder));
	}

	@Override
	/**
	 * Sobrescribe a la función base por incompatibilidad de query. Función que
	 * dado un conjunto de términos, nos devuelve una query de elasticsearch.
	 * Debe estar implementado en cada repositorio para darle una funcionalidad
	 * específica y aquí estarán las funcionalidades que comparten todos los
	 * repositorios.
	 * 
	 * @param terms
	 *            Map de términos pasados por la query.
	 * @param query
	 *            QueryBuilder con la query de los términos acumulados en los
	 *            repositorios específicos.
	 * @return query de tipo terms de elasticsearch.
	 */
	@SuppressWarnings("unchecked")
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("selection")) {
			String selectedId = (String) terms.get("selection");

			Map<String, Object> selected = findSelectedItems(selectedId);

			if (selected != null) {
				List<String> ids = (List<String>) (List<?>) selected.get("ids");
				if (ids != null && ids.size() > 0) {
					String term = "path.split";
					if (ids.get(0).contains("."))
						term = "path";
					query.must(QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery(term, ids)));
				}
			}
			terms.remove("selection");
		}

		if (terms.containsKey("ids") || terms.containsKey("paths")) {
			BoolQueryBuilder filter = QueryBuilders.boolQuery();
			if (terms.containsKey("ids")) {
				List<String> ids = (List<String>) (List<?>) terms.get("ids");
				filter.should(QueryBuilders.termsQuery("path.split", ids));
			}
			if (terms.containsKey("paths")) {
				List<String> paths = (List<String>) (List<?>) terms.get("paths");
				filter.should(QueryBuilders.termsQuery("path", paths));
			}

			if (terms.containsKey("presentPaths")) {
				List<String> paths = (List<String>) (List<?>) terms.get("presentPaths");
				filter.mustNot(QueryBuilders.termsQuery("path", paths));
			}
			query.must(filter);
		}
		return super.getTermQuery(terms, query);
	}
}
