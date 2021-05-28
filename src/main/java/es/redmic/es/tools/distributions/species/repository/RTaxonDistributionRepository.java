package es.redmic.es.tools.distributions.species.repository;

/*-
 * #%L
 * ElasticSearch
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.geo.builders.EnvelopeBuilder;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import es.redmic.es.common.repository.RBaseESRepository;
import es.redmic.es.geodata.citation.repository.CitationESRepository;
import es.redmic.es.geodata.tracking.animal.repository.AnimalTrackingESRepository;
import es.redmic.exception.elasticsearch.ESBBoxQueryException;
import es.redmic.exception.elasticsearch.ESQueryException;
import es.redmic.models.es.common.query.dto.BboxQueryDTO;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;
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

	private static String COORDINATES_FIELD = "geometry";
	private static String TAXON_ID_FIELD = "properties.taxons.path";
	private static String TAXON_EQUIVALENT_ID_FIELD = "properties.taxons.equivalent";
	private static String TAXON_PATH_FIELD = "properties.taxons";
	private static String REGISTERS_PATH_FIELD = "properties.taxons.registers";
	private static String REGISTERS_MISIDENTIFICATION_FIELD = "properties.taxons.registers.misidentification";
	private static String REGISTERS_CONFIDENCE_FIELD = "properties.taxons.registers.confidence";

	private static String AGGS_DISTRIBUTION_SCRIPT_PATH = "/scripts/aggs-distribution.txt";

	private static final String SCRIPT_LANG = "painless";

	private static String[] INCLUDE_DEFAULT = new String[] { "*" };
	private static String[] EXCLUDE_DEFAULT = new String[] {};

	@Autowired
	AnimalTrackingESRepository animalTrackingRepository;

	@Autowired
	CitationESRepository citationESRepository;

	public RTaxonDistributionRepository() {
	}

	public RTaxonDistributionRepository(String[] index, String type) {
		super(index, type);
	}

	@Override
	protected String getMappingFilePath(String index, String type) {
		return MAPPING_BASE_PATH + "taxon/distribution" + MAPPING_FILE_EXTENSION;
	}

	public Integer getGridSize() {
		return null;
	}

	public GeoJSONFeatureCollectionDTO findAll(GeoDataQueryDTO dto, List<String> ids) {

		GeoJSONFeatureCollectionDTO res = new GeoJSONFeatureCollectionDTO();

		List<Integer> confidence = getConfidenceValues(dto);
		if (confidence == null || confidence.isEmpty() || confidence.size() > 4)
			return res;

		Map<String, Object> scriptParams = new HashMap<>();
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

		List<Object> ret = new ArrayList<>();

		for (SearchHit obj : result) {
			Map<String, Object> properties = (Map<String, Object>) obj.getSourceAsMap().get("properties");
			Integer registerCount = (Integer) properties.get("registerCount");
			if (registerCount != null && registerCount > 0)
				ret.add(obj.getSourceAsMap());
		}
		return ret;
	}

	public SearchResponse findAll(QueryBuilder query, String[] include, String[] exclude,
			Map<String, Object> scriptParams) {

		SearchRequest searchRequest = new SearchRequest(getIndex());
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		searchSourceBuilder.query(query);
		searchSourceBuilder.size(10000);
		searchSourceBuilder.fetchSource(include, exclude);
		searchSourceBuilder.scriptField("taxons", new Script(ScriptType.INLINE, SCRIPT_LANG, getFilterScript(), scriptParams));

		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse;

		try {
			searchResponse = ESProvider.getClient().search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESQueryException();
		}

		return searchResponse;
	}

	private QueryBuilder createQuery(GeoDataQueryDTO queryDTO, List<String> ids, List<Integer> confidence) {

		NestedQueryBuilder idsFilter;
		if (ids != null && !ids.isEmpty()) // TODO: quitar, siempre debe ids
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
							.geoShapeQuery(COORDINATES_FIELD, new EnvelopeBuilder(topLeft, bottomRight))));
		} catch (IOException e) {
			throw new ESBBoxQueryException(e);
		}

		return query;
	}

	public TaxonDistribution findByRegisterId(String id) {

		id = id.replaceFirst("^/activity/\\d+", "");

		QueryBuilder query = QueryBuilders.boolQuery()
				.filter(QueryBuilders.nestedQuery(REGISTERS_PATH_FIELD,
						QueryBuilders.boolQuery().must(QueryBuilders.prefixQuery(REGISTERS_PATH_FIELD + ".id", id)),
						ScoreMode.Avg));

		SearchResponse response = super.searchRequest(query);

		if (response.getHits().getHits().length > 0)
			return objectMapper.convertValue(response.getHits().getHits()[0].getSourceAsMap(), TaxonDistribution.class);
		return null;

	}

	@SuppressWarnings("unchecked")
	public List<TaxonDistributionRegistersDTO> findByGridIdAndTaxons(GeoDataQueryDTO queryDTO, String gridId,
			List<String> taxonIds) {

		List<Integer> confidence = getConfidenceValues(queryDTO);
		if (confidence == null || confidence.isEmpty() || confidence.size() > 4)
			return new ArrayList<>();

		NestedQueryBuilder confidencesFilter = QueryBuilders.nestedQuery(REGISTERS_PATH_FIELD, QueryBuilders.boolQuery()
				.must(QueryBuilders.termsQuery(REGISTERS_PATH_FIELD + ".confidence", confidence)), ScoreMode.Avg);

		QueryBuilder query = QueryBuilders.boolQuery().filter(confidencesFilter)
				.must(QueryBuilders.termQuery("id", gridId));

		SearchResponse response = super.searchRequest(query);

		if (response.getHits().getTotalHits() == 0)
			return new ArrayList<>();

		List<TaxonDistributionRegistersDTO> registers = new ArrayList<>();

		Distribution result = objectMapper.convertValue(response.getHits().getHits()[0].getSourceAsMap(),
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
	private List<Integer> getConfidenceValues(GeoDataQueryDTO dto) {

		if (dto.getTerms() == null || dto.getTerms().get("confidences") == null)
			return new ArrayList<>();

		List<Integer> confidence = (List<Integer>) dto.getTerms().get("confidences");
		if (confidence.isEmpty() || confidence.size() > 4)
			return new ArrayList<>();
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
		register.getProperties().setActivityId(result.get_source().getProperties().getActivityId());

		return register;
	}

	protected GetResponse findById(String id) {

		return super.getRequest(id);
	}

	// TODO: crear IProcessItemFunction y ejecutar el de la base
	@Override
	protected List<Distribution> scrollQueryReturnItems(QueryBuilder builder) {

		List<Distribution> result = new ArrayList<>();

		SearchRequest searchRequest = new SearchRequest(getIndex());

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		searchSourceBuilder.size(100);

		searchRequest.source(searchSourceBuilder);
		searchRequest.scroll(new TimeValue(60000));

		SearchResponse searchResponse;
		try {
			searchResponse = ESProvider.getClient().search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESQueryException();
		}

		while (true) {

			for (SearchHit hit : searchResponse.getHits().getHits()) {
				result.add(objectMapper.convertValue(hit.getSourceAsMap(), typeOfTModel));
			}
			SearchScrollRequest scrollRequest = new SearchScrollRequest(searchResponse.getScrollId());
			scrollRequest.scroll(new TimeValue(600000));

			try {
				searchResponse = ESProvider.getClient().scroll(scrollRequest, RequestOptions.DEFAULT);
			} catch (IOException e) {
				e.printStackTrace();
				throw new ESQueryException();
			}

			if (searchResponse.getHits().getHits().length == 0)
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

	private String getFilterScript() {

		try {
			return new String(Files.readAllBytes(Paths.get(AGGS_DISTRIBUTION_SCRIPT_PATH)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ESQueryException();
		}

	}
}
