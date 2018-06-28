package es.redmic.es.tools.distributions.species.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.geo.builders.ShapeBuilders;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

import es.redmic.es.common.repository.RBaseESRepository;
import es.redmic.es.geodata.citation.repository.CitationESRepository;
import es.redmic.es.geodata.tracking.animal.repository.AnimalTrackingESRepository;
import es.redmic.exception.elasticsearch.ESBBoxQueryException;
import es.redmic.models.es.common.query.dto.BboxQueryDTO;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.geojson.citation.dto.CitationDTO;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.tracking.animal.dto.AnimalTrackingDTO;
import es.redmic.models.es.tools.distribution.dto.TaxonDistributionRegistersDTO;
import es.redmic.models.es.tools.distribution.model.Distribution;
import es.redmic.models.es.tools.distribution.model.Register;
import es.redmic.models.es.tools.distribution.species.model.TaxonDistribution;

@Component
public class RTaxonDistributionRepository extends RBaseESRepository<Distribution> {

	protected String[] INDEX;
	protected String[] TYPE;

	protected static String SCRIPT_ENGINE = "groovy";

	private static String COORDINATES_FIELD = "geometry";
	private static String TAXON_ID_FIELD = "properties.taxons.path";
	private static String TAXON_EQUIVALENT_ID_FIELD = "properties.taxons.equivalent";
	private static String TAXON_PATH_FIELD = "properties.taxons";
	private static String REGISTERS_PATH_FIELD = "properties.taxons.registers";
	private static String REGISTERS_MISIDENTIFICATION_FIELD = "properties.taxons.registers.misidentification";
	private static String REGISTERS_CONFIDENCE_FIELD = "properties.taxons.registers.confidence";

	private static String FILTER_SCRIPT = "aggs-dist";

	private static String[] INCLUDE_DEFAULT = new String[] { "*" };
	private static String[] EXCLUDE_DEFAULT = new String[] {};

	@Autowired
	AnimalTrackingESRepository animalTrackingRepository;

	@Autowired
	CitationESRepository citationESRepository;

	public RTaxonDistributionRepository() {
	}

	public RTaxonDistributionRepository(String[] INDEX, String[] TYPE) {
		this.INDEX = INDEX;
		this.TYPE = TYPE;
	}

	public Integer getGridSize() {
		return null;
	}

	public GeoJSONFeatureCollectionDTO findAll(DataQueryDTO dto, List<String> ids) {

		GeoJSONFeatureCollectionDTO res = new GeoJSONFeatureCollectionDTO();

		List<Integer> confidence = getConfidenceValues(dto);
		if (confidence == null || confidence.size() == 0 || confidence.size() > 4)
			return res;

		Map<String, Object> scriptParams = new HashMap<String, Object>();
		scriptParams.put("taxons", ids);
		scriptParams.put("confidences", confidence);
		SearchResponse response = findAll(createQuery(dto, ids, confidence), INCLUDE_DEFAULT, EXCLUDE_DEFAULT,
				scriptParams);

		if (response != null) {
			List<Object> result = mapperHitsArrayToClass(response.getHits().getHits());
			res.setFeatures(result);

		}
		return res;
	}

	/**
	 * Función para extraer los features del geojson, en este caso sin añadir
	 * las properties ya que no son necesarias.
	 * 
	 * @param result
	 *            lista de resultados obtenidos en la consulta.
	 * @return lista de features para distribution, las cuales contienen la
	 *         geomtría y el id de la rejilla.
	 */
	@SuppressWarnings("unchecked")
	private List<Object> mapperHitsArrayToClass(SearchHit[] result) {

		List<Object> ret = new ArrayList<Object>();

		for (SearchHit obj : result) {
			Map<String, Object> properties = (Map<String, Object>) obj.getSource().get("properties");
			Integer registerCount = (Integer) properties.get("registerCount");
			if (registerCount != null && registerCount > 0)
				ret.add(obj.getSource());
		}
		return ret;
	}

	public SearchResponse findAll(QueryBuilder query, String[] include, String[] exclude,
			Map<String, Object> scriptParams) {

		SearchRequestBuilder requestBuilder = ESProvider.getClient().prepareSearch(INDEX)
				.setFetchSource(include, exclude).setTypes(TYPE).setQuery(query).setSize(10000)
				.addScriptField("taxons", new Script(ScriptType.FILE, SCRIPT_ENGINE, FILTER_SCRIPT, scriptParams));
		return requestBuilder.execute().actionGet();
	}

	private QueryBuilder createQuery(DataQueryDTO queryDTO, List<String> ids, List<Integer> confidence) {

		NestedQueryBuilder idsFilter;
		if (ids != null && ids.size() > 0) // TODO: quitar, siempre debe ids
			idsFilter = QueryBuilders.nestedQuery(TAXON_PATH_FIELD, QueryBuilders.boolQuery()
					.should(QueryBuilders.termsQuery(TAXON_ID_FIELD, ids))
					.should(QueryBuilders.termsQuery(TAXON_EQUIVALENT_ID_FIELD, ids))
					.should(QueryBuilders.nestedQuery(REGISTERS_PATH_FIELD,
							QueryBuilders.boolQuery().must(
									QueryBuilders.termsQuery(REGISTERS_MISIDENTIFICATION_FIELD, ids)),
							ScoreMode.Avg)),
					ScoreMode.Avg);
		else
			return null;

		NestedQueryBuilder confidenceFilter = QueryBuilders.nestedQuery(REGISTERS_PATH_FIELD,
				QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(REGISTERS_CONFIDENCE_FIELD, confidence)),
				ScoreMode.Avg);

		BboxQueryDTO bbox = queryDTO.getBbox();

		if (bbox == null)
			throw new ESBBoxQueryException(new NullPointerException());

		Coordinate topLeft = new Coordinate(bbox.getTopLeftLon(), bbox.getTopLeftLat());
		Coordinate bottomRight = new Coordinate(bbox.getBottomRightLon(), bbox.getBottomRightLat());

		BoolQueryBuilder query;
		try {
			query = QueryBuilders.boolQuery()
					.filter(QueryBuilders.boolQuery().must(idsFilter).must(confidenceFilter).must(QueryBuilders
							.geoShapeQuery(COORDINATES_FIELD, ShapeBuilders.newEnvelope(topLeft, bottomRight))));
		} catch (IOException e) {
			throw new ESBBoxQueryException(e);
		}

		return query;
	}

	public TaxonDistribution findByRegisterId(String id) {

		id = id.replaceFirst("^/activity/\\d+", "");

		QueryBuilder builder = QueryBuilders.boolQuery()
				.filter(QueryBuilders.nestedQuery(REGISTERS_PATH_FIELD,
						QueryBuilders.boolQuery().must(QueryBuilders.prefixQuery(REGISTERS_PATH_FIELD + ".id", id)),
						ScoreMode.Avg));

		SearchRequestBuilder requestBuilder = ESProvider.getClient().prepareSearch(INDEX).setTypes(TYPE)
				.setQuery(builder);
		SearchResponse response = requestBuilder.execute().actionGet();
		if (response.getHits().getHits().length > 0)
			return objectMapper.convertValue(response.getHits().getHits()[0].getSource(), TaxonDistribution.class);
		return null;

	}

	@SuppressWarnings("unchecked")
	public List<TaxonDistributionRegistersDTO> findByGridIdAndTaxons(DataQueryDTO queryDTO, String gridId,
			List<String> taxonIds) {

		List<Integer> confidence = getConfidenceValues(queryDTO);
		if (confidence == null || confidence.size() == 0 || confidence.size() > 4)
			return new ArrayList<>();

		NestedQueryBuilder confidencesFilter = QueryBuilders.nestedQuery(REGISTERS_PATH_FIELD, QueryBuilders.boolQuery()
				.must(QueryBuilders.termsQuery(REGISTERS_PATH_FIELD + ".confidence", confidence)), ScoreMode.Avg);

		QueryBuilder builder = QueryBuilders.boolQuery().filter(confidencesFilter)
				.must(QueryBuilders.termQuery("id", gridId));

		SearchRequestBuilder requestBuilder = ESProvider.getClient().prepareSearch(INDEX).setTypes(TYPE)
				.setQuery(builder);
		SearchResponse response = requestBuilder.execute().actionGet();

		if (response.getHits().getTotalHits() == 0)
			return new ArrayList<>();

		List<TaxonDistributionRegistersDTO> registers = new ArrayList<TaxonDistributionRegistersDTO>();

		Distribution result = objectMapper.convertValue(response.getHits().getHits()[0].getSource(),
				Distribution.class);

		for (int i = 0; i < result.getProperties().getTaxons().size(); i++) {
			TaxonDistribution taxonDistribution = result.getProperties().getTaxons().get(i);
			TaxonDistributionRegistersDTO register = null;

			for (int j = 0; j < taxonDistribution.getRegisters().size(); j++) {

				Register reg = taxonDistribution.getRegisters().get(j);

				if (fulfilQueryRestrictions(taxonIds, confidence, taxonDistribution.getPath(),
						taxonDistribution.getEquivalent(), reg)) {

					String regId = reg.getId();
					String[] regIdSplit = regId.split("/");
					String id = regIdSplit[4];
					String parentId = regIdSplit[2];

					if (regId.contains("animaltracking")) {

						Feature<GeoDataProperties, Point> animalTracking = getGeoData(
								(GeoHitWrapper<GeoDataProperties, Point>) animalTrackingRepository.findById(id,
										parentId));
						if (register == null) {
							register = orikaMapper.getMapperFacade().map(
									animalTracking.getProperties().getCollect().getTaxon(),
									TaxonDistributionRegistersDTO.class);
						}
						register.getAnimalTrackings()
								.add(orikaMapper.getMapperFacade().map(animalTracking, AnimalTrackingDTO.class));

					} else if (regId.contains("citation")) {

						Feature<GeoDataProperties, Point> citation = getGeoData(
								(GeoHitWrapper<GeoDataProperties, Point>) citationESRepository.findById(id, parentId));
						if (register == null) {
							register = orikaMapper.getMapperFacade().map(
									citation.getProperties().getCollect().getTaxon(),
									TaxonDistributionRegistersDTO.class);
						}
						if (citation.getProperties().getCollect().getMisidentification() != null)
							register.setMisidentification(true);

						register.getCitations().add(orikaMapper.getMapperFacade().map(citation, CitationDTO.class));
					}
				}
			}
			if (register != null)
				registers.add(register);
		}

		return registers;
	}

	@SuppressWarnings("unchecked")
	private List<Integer> getConfidenceValues(DataQueryDTO dto) {

		if (dto.getTerms() == null || dto.getTerms().get("confidences") == null)
			return null;

		List<Integer> confidence = (List<Integer>) dto.getTerms().get("confidences");
		if (confidence.size() == 0 || confidence.size() > 4)
			return null;
		return confidence;
	}

	private Boolean fulfilQueryRestrictions(List<String> taxonIds, List<Integer> confidences, String taxonPath,
			String taxonEquivalent, Register reg) {

		return (taxonIds.contains(taxonPath) || taxonIds.contains(taxonEquivalent)
				|| taxonIds.contains(reg.getMisidentification()))
				&& confidences.contains(reg.getConfidence().intValue());
	}

	protected Feature<GeoDataProperties, Point> getGeoData(GeoHitWrapper<GeoDataProperties, Point> result) {
		Feature<GeoDataProperties, Point> register = result.get_source();
		register.set_parentId(result.get_parent());

		return register;
	}

	protected GetResponse findById(String id) {

		int sizeIndex = INDEX.length;
		int sizeType = TYPE.length;
		for (int i = 0; i < sizeType; i++) {
			GetResponse result = ESProvider.getClient().prepareGet(INDEX[0], TYPE[i], id.toString()).execute()
					.actionGet();
			if (result.isExists())
				return result;
		}
		for (int j = 0; j < sizeIndex; j++) {
			GetResponse result = ESProvider.getClient().prepareGet(INDEX[j], TYPE[0], id.toString()).execute()
					.actionGet();
			if (result.isExists())
				return result;
		}
		return null;
	}

	@Override
	protected List<Distribution> scrollQueryReturnItems(QueryBuilder builder) {

		List<Distribution> result = new ArrayList<Distribution>();

		SearchResponse scrollResp = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setScroll(new TimeValue(60000)).setQuery(builder).setSize(100).execute().actionGet();

		while (true) {

			for (SearchHit hit : scrollResp.getHits().getHits()) {
				Distribution item = objectMapper.convertValue(hit.getSourceAsMap(), typeOfTModel);
				result.add(item);
			}
			scrollResp = ESProvider.getClient().prepareSearchScroll(scrollResp.getScrollId())
					.setScroll(new TimeValue(600000)).execute().actionGet();

			if (scrollResp.getHits().getHits().length == 0)
				break;
		}
		return result;
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

	@Override
	protected JavaType getSourceType(Class<?> wrapperClass) {
		// TODO: Implementar cuando cambie distribution
		return null;
	}
}