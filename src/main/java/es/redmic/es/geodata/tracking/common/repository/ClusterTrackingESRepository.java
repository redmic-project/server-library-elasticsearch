package es.redmic.es.geodata.tracking.common.repository;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import es.redmic.es.common.queryFactory.geodata.DataQueryUtils;
import es.redmic.es.common.repository.ProcessClusterMultiElementFunction;
import es.redmic.es.geodata.common.repository.GeoPresenceESRepository;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.tracking.common.BaseTrackingClusterDTO;

public abstract class ClusterTrackingESRepository<TModel extends Feature<GeoDataProperties, ?>>
		extends GeoPresenceESRepository<TModel> {

	protected final static String BASE_PATH = "properties.inTrack", DATE_PATH = "properties.inTrack.date",
			PLATFORM_PATH = "properties.inTrack.platform.uuid", ANIMAL_PATH = "properties.collect.animal.uuid",
			QFLAG_PATH = "properties.inTrack.qFlag", ACCEPTED_QFLAG = "1";

	public ClusterTrackingESRepository() {
		super();
	}

	public GeoJSONFeatureCollectionDTO getTrackingPointsInLineStringCluster(String parentId, DataQueryDTO queryDTO) {
		return getTrackingPointsInLineStringCluster(parentId, queryDTO, null);
	}

	public GeoJSONFeatureCollectionDTO getTrackingPointsInLineStringCluster(String parentId, DataQueryDTO queryDTO,
			String uuid) {

		int zoomLevel = 7;

		if (queryDTO.getTerms() != null && queryDTO.getTerms().containsKey("zoomLevel"))
			zoomLevel = (int) queryDTO.getTerms().get("zoomLevel");

		BoolQueryBuilder queryParent = QueryBuilders.boolQuery()
				.must(DataQueryUtils.getQueryByParent(parentId));
			
		if (uuid != null && !uuid.isEmpty()) {
			queryParent.must(QueryBuilders.boolQuery().should(QueryBuilders.termsQuery(PLATFORM_PATH, uuid))
						.should(QueryBuilders.termsQuery(ANIMAL_PATH, uuid)));
		}
		
		BoolQueryBuilder builder = QueryBuilders.boolQuery()
				.filter(queryParent);

		QueryBuilder termQuery = getTermQuery(queryDTO.getTerms());

		if (termQuery != null)
			builder.must(termQuery);

		BoolQueryBuilder queryBuilder = getQuery(queryDTO, getInternalQuery(), builder);

		@SuppressWarnings("unchecked")
		List<BaseTrackingClusterDTO> result = (List<BaseTrackingClusterDTO>) scrollQueryReturnItems(queryBuilder,
				new ProcessClusterMultiElementFunction(objectMapper, zoomLevel));

		GeoJSONFeatureCollectionDTO collection = new GeoJSONFeatureCollectionDTO();

		collection.setFeatures(result);

		return collection;
	}

	/**
	 * Función que devuelve una lista de ordenaciones. Debe estar implementado
	 * en cada repositorio para darle una funcionalidad específica. Por defecto,
	 * ordena por id.
	 * 
	 * @return lista de ordenaciones de elasticsearch
	 */
	protected List<SortBuilder<?>> getSort() {
		List<SortBuilder<?>> sorts = new ArrayList<SortBuilder<?>>();
		sorts.add(SortBuilders.fieldSort(PLATFORM_PATH).order(SortOrder.ASC));
		sorts.add(SortBuilders.fieldSort(ANIMAL_PATH).order(SortOrder.ASC));
		sorts.add(SortBuilders.fieldSort(DATE_PATH).order(SortOrder.ASC));

		return sorts;
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "properties.collect.animal.name.suggest", "properties.inTrack.platform.name.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "properties.collect.animal.name.suggest", "properties.inTrack.platform.name.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "properties.collect.animal.name", "properties.inTrack.platform.name" };
	}
}