package es.redmic.es.geodata.common.repository;

import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import es.redmic.es.common.repository.SelectionWorkRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.exception.elasticsearch.ESTooManySelectedItemsException;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;

public abstract class GeoPresenceESRepository<TModel extends Feature<GeoDataProperties, ?>>
		extends GeoDataESRepository<TModel> {

	@Value("${redmic.elasticsearch.MAX_QUERY_SIZE}")
	Integer maxQuerySize;

	private static String[] TAXON_PATH_FIELDS = { "properties.collect.taxon.path",
			"properties.collect.misidentification.taxon.path", "properties.collect.taxon.validAs.path" };

	private static String TAXON_PRECISION_FIELD = "properties.collect.radius";

	@Autowired
	SelectionWorkRepository selectionWorkRepository;

	public GeoPresenceESRepository() {
	}

	/**
	 * Función que sobrescribe a getTermQuery de RElasticSearchRepository para
	 * añadir implementación específica para crear una query a apartir de una
	 * serie de términos obtenidos por el controlador.
	 */

	@SuppressWarnings("unchecked")
	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("taxonId")) {

			BoolQueryBuilder idsFilter = QueryBuilders.boolQuery();
			String id = (String) terms.get("taxonId"), suffix = getSuffixQueryPath(id);

			for (int i = 0; i < TAXON_PATH_FIELDS.length; i++)
				idsFilter.should(QueryBuilders.termQuery(TAXON_PATH_FIELDS[i] + "." + suffix, id));

			query.must(idsFilter);
		}
		// TODO: eliminar cuando se envie a primer nivel
		if (terms.containsKey("precision")) {

			List<Double> precision = (List<Double>) terms.get("precision");
			query.must(QueryBuilders.rangeQuery(TAXON_PRECISION_FIELD).from(precision.get(0)).to(precision.get(1)));
		}
		if (terms.containsKey("selection")) {

			String selectionId = (String) terms.get("selection");
			List<String> ids = (List<String>) selectionWorkRepository.getSelectedIds(selectionId);

			terms.remove("selection");
			if (ids != null && ids.size() > 0) {

				if (ids.size() > maxQuerySize)
					throw new ESTooManySelectedItemsException();

				BoolQueryBuilder idsFilter = QueryBuilders.boolQuery();
				String suffix = getSuffixQueryPath(ids.get(0));

				for (int i = 0; i < TAXON_PATH_FIELDS.length; i++)
					idsFilter.should(QueryBuilders.termsQuery(TAXON_PATH_FIELDS[i] + "." + suffix, ids));

				query.must(idsFilter);
			} else { // TODO: añadir excepción específica
				LOGGER.debug("Error. No se puede hacer query en geoPresence sin elementos seleccionados");
				throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
			}
		}
		return super.getTermQuery(terms, query);
	}

	private String getSuffixQueryPath(String id) {

		if (id == null)
			return null;

		String[] pathSplit = id.split("\\.");
		if (pathSplit.length > 1)
			return "hierarchy";
		else
			return "split";
	}
}