package es.redmic.es.series.timeseries.repository;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BaseAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.queryFactory.series.SeriesQueryUtils;
import es.redmic.es.series.common.repository.RWSeriesESRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.dto.LimitsDTO;
import es.redmic.models.es.common.query.dto.AggsPropertiesDTO;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.geojson.common.model.Aggregations;
import es.redmic.models.es.series.common.model.SeriesSearchWrapper;
import es.redmic.models.es.series.timeseries.dto.DatesByDirectionListDTO;
import es.redmic.models.es.series.timeseries.model.TimeSeries;

@Repository
public class TimeSeriesESRepository extends RWSeriesESRepository<TimeSeries> {

	private static String[] INDEX = { "activity" };
	private static String[] TYPE = { "timeseries" };

	protected static String SCRIPT_ENGINE = "groovy", directionRangeScript = "move-direction-degrees";

	public TimeSeriesESRepository() {
		super(INDEX, TYPE);
	}

	@SuppressWarnings({ "serial", "unchecked" })
	public Aggregations getStatAggs(DataQueryDTO query, Integer speedDataDefinition) {

		// Crea query para obtener max, min y count de velocidades
		query.setSize(0);
		query.getTerms().put("dataDefinition", new ArrayList<Integer>() {
			{
				add(speedDataDefinition);
			}
		});
		// añade identificador para que se cree la agregación correspondinte en
		// el repositorio
		query.addAgg(new AggsPropertiesDTO("stats"));

		SeriesSearchWrapper<TimeSeries> responseStats = (SeriesSearchWrapper<TimeSeries>) find(query);

		if (responseStats.getAggregations() == null || responseStats.getAggregations().getAttributes() == null) {
			LOGGER.debug("No es posible realizar los cálculos");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}

		return responseStats.getAggregations();
	}

	@SuppressWarnings({ "serial", "unchecked" })
	public Aggregations getDatesByDirectionAggs(DataQueryDTO query, Integer numSectors,
			Integer directionDataDefinition) {

		// añade identificador para que se cree la agregación de los rangos de
		// los sectores en el repositorio
		// (Los sectores se calculan en grados a partir del número de sectores
		// enviado por el cliente)
		query.getAggs().clear();
		query.addAgg(new AggsPropertiesDTO("sectors"));
		query.getTerms().put("numSectors", numSectors);

		query.getTerms().put("dataDefinition", new ArrayList<Integer>() {
			{
				add(directionDataDefinition);
			}
		});

		SeriesSearchWrapper<TimeSeries> responseSectors = (SeriesSearchWrapper<TimeSeries>) find(query);

		if (responseSectors == null || responseSectors.getAggregations() == null) {
			LOGGER.debug("No es posible realizar los cálculos");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}

		return responseSectors.getAggregations();
	}

	public List<SeriesSearchWrapper<?>> getWindroseData(DataQueryDTO query,
			DatesByDirectionListDTO datesByDirectionListDTO, Integer count) {

		List<SearchRequestBuilder> searchs = new ArrayList<SearchRequestBuilder>();

		// para cada uno de los sectores obtener queryBuilder enviando en terms
		// las fechas.
		for (int i = 0; i < datesByDirectionListDTO.size(); i++) {
			// TODO: optimizar evitando hacer query cuando
			// datesByDirectionListDTO.get(i).getDates() == 0
			query.getTerms().put("dates", datesByDirectionListDTO.get(i).getDates());
			searchs.add(searchRequestBuilder(query));
		}
		return multiFind(searchs);
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	protected List<BaseAggregationBuilder> getAggs(DataQueryDTO elasticQueryDTO) {

		List<AggsPropertiesDTO> aggs = elasticQueryDTO.getAggs();

		if (elasticQueryDTO.getInterval() == null && (aggs == null || aggs.size() == 0))
			return null;

		List<BaseAggregationBuilder> aggsBuilder = new ArrayList<BaseAggregationBuilder>();

		if (aggs.get(0) == null)
			return null;

		if (aggs.get(0).getField().equals("windrose")) {

			aggsBuilder.add(getWindroseAggregationBuilder(elasticQueryDTO));

		} else if (aggs.get(0).getField().equals("sectors")) {

			aggsBuilder.add(getSectorAggregationBuilder(elasticQueryDTO));

		} else if (aggs.get(0).getField().equals("stats")) {
			// estadísticas para obtener el min, max y count de la velocidad
			// (Query 1 de windrose)
			aggsBuilder.add(AggregationBuilders.stats(defaultField).field(defaultField));

		} else if (elasticQueryDTO.getInterval() != null && (aggs.get(0).getField().equals("temporaldata"))) {

			aggsBuilder.add(getTemporalDataAggregationBuilder(elasticQueryDTO));
		}
		return aggsBuilder;
	}

	@SuppressWarnings("unchecked")
	/*
	 * Obtener count y avg de velocidad agrupadas por rangos de precisión (Query
	 * final por cada sector)
	 * 
	 */
	private RangeAggregationBuilder getWindroseAggregationBuilder(DataQueryDTO elasticQueryDTO) {

		List<LimitsDTO> limits = (List<LimitsDTO>) elasticQueryDTO.getTerms().get("limits");

		RangeAggregationBuilder range = AggregationBuilders.range("value_ranges").field(defaultField);

		for (int i = 0; i < limits.size(); i++) {
			if (i == (limits.size() - 1)) // Rango abierto para el último
				range.addUnboundedFrom(limits.get(i).getMin());
			else
				range.addRange(limits.get(i).getMin(), limits.get(i).getMax());
		}

		return range.subAggregation(AggregationBuilders.count("count").field(defaultField));
	}

	/*
	 * Obtener fechas de datos agrupadas por sectores (Query 2 de windrose)
	 */
	private RangeAggregationBuilder getSectorAggregationBuilder(DataQueryDTO elasticQueryDTO) {

		Integer numSectors = (Integer) elasticQueryDTO.getTerms().get("numSectors");

		Double sectorLength = (double) (360 / numSectors);

		Map<String, Object> fields = new HashMap<String, Object>();
		fields.put("degrees", sectorLength / 2);
		RangeAggregationBuilder range = AggregationBuilders.range("direction_ranges").field(defaultField)
				.script(new Script(ScriptType.FILE, SCRIPT_ENGINE, directionRangeScript, fields));
		double limit = 0;
		for (int i = 0; i < numSectors; i++) {

			if (i == (numSectors - 1)) // Rango abierto para el último
				range.addUnboundedFrom(limit);
			else
				range.addRange(limit, limit + sectorLength);
			limit += sectorLength;
		}

		return range.subAggregation(AggregationBuilders.terms("dates").field(dateTimeField).size(MAX_SIZE));
	}

	private DateHistogramAggregationBuilder getTemporalDataAggregationBuilder(DataQueryDTO elasticQueryDTO) {

		return getDateHistogramAggregation(SeriesQueryUtils.getInterval(elasticQueryDTO.getInterval()))
				.subAggregation(AggregationBuilders.stats(defaultField).field(defaultField));
	}

	@SuppressWarnings("unchecked")
	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {

		if (terms.containsKey("dates")) {
			List<String> dates = (List<String>) (List<?>) terms.get("dates");
			query.must(QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery("date", dates)));
		}
		return super.getTermQuery(terms, query);
	}
}
