package es.redmic.es.series.timeseries.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.geodata.geofixedstation.service.GeoFixedTimeSeriesESService;
import es.redmic.es.series.common.service.RWSeriesESService;
import es.redmic.es.series.common.utils.DataDefinitionUtils;
import es.redmic.es.series.timeseries.repository.TimeSeriesESRepository;
import es.redmic.exception.elasticsearch.ESTermQueryException;
import es.redmic.models.es.common.dto.ElasticSearchDTO;
import es.redmic.models.es.common.dto.LimitsDTO;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.query.dto.AggsPropertiesDTO;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.geojson.common.model.Aggregations;
import es.redmic.models.es.maintenance.parameter.model.DataDefinition;
import es.redmic.models.es.series.common.model.SeriesSearchWrapper;
import es.redmic.models.es.series.timeseries.dto.DataHistogramDTO;
import es.redmic.models.es.series.timeseries.dto.DatesByDirectionListDTO;
import es.redmic.models.es.series.timeseries.dto.RawDataDTO;
import es.redmic.models.es.series.timeseries.dto.RawDataItemDTO;
import es.redmic.models.es.series.timeseries.dto.TimeSeriesDTO;
import es.redmic.models.es.series.timeseries.dto.WindroseDataDTO;
import es.redmic.models.es.series.timeseries.dto.WindroseSectorDTO;
import es.redmic.models.es.series.timeseries.model.TimeSeries;

@Service
public class TimeSeriesESService extends RWSeriesESService<TimeSeries, TimeSeriesDTO> {

	private final static Integer PARTITION_NUMBER_LIMIT = 20;

	@Autowired
	GeoFixedTimeSeriesESService geoFixedTimeSeriesESSevice;

	private TimeSeriesESRepository repository;

	@Autowired
	public TimeSeriesESService(TimeSeriesESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	@Override
	public TimeSeries mapper(TimeSeriesDTO dtoToIndex) {
		return orikaMapper.getMapperFacade().map(dtoToIndex, TimeSeries.class);
	}

	@Override
	protected void postUpdate(ReferencesES<TimeSeries> reference) {
	}

	@Override
	protected void postSave(Object model) {
	}

	@Override
	protected void preDelete(Object object) {
	}

	@Override
	protected void postDelete(String id) {
	}

	@SuppressWarnings("unchecked")
	public ElasticSearchDTO findTemporalDataStatistics(DataQueryDTO query) {

		if (query.getInterval() == null)
			DataDefinitionUtils.addDataDefinitionFieldToReturn(query);

		SeriesSearchWrapper<TimeSeries> response = (SeriesSearchWrapper<TimeSeries>) repository.find(query);
		List<Integer> dataDefinitionIds = (List<Integer>) query.getTerms().get("dataDefinition");

		if (query.getInterval() != null) {

			DataHistogramDTO dtoOut = orikaMapper.getMapperFacade().convert(response.getAggregations(),
					DataHistogramDTO.class, null);
			dtoOut.setDataDefinitionIds((List<Integer>) query.getTerms().get("dataDefinition"));
			return new ElasticSearchDTO(dtoOut, dtoOut.getData().size());
		}

		RawDataDTO result = getSourceToResult(response, dataDefinitionIds);

		return new ElasticSearchDTO(result, result.getData().size());
	}

	@SuppressWarnings({ "unchecked" })
	public ElasticSearchDTO getWindRoseData(DataQueryDTO query) {

		// Obtiene datos de la query

		Map<String, Object> dataDefinitionMap = (Map<String, Object>) query.getTerms().get("dataDefinition");

		Integer numSectors = (Integer) query.getTerms().get("numSectors"),
				partitionNumber = (Integer) query.getTerms().get("numSplits"),
				speedDataDefinition = (Integer) dataDefinitionMap.get("speed"),
				directionDataDefinition = (Integer) dataDefinitionMap.get("direction");

		checkValidNumSectors(numSectors);
		checkValidPartitionNumber(partitionNumber);

		Map<String, Object> aggs = getStatAggs(query, speedDataDefinition);

		Double min = (Double) aggs.get("min"), max = (Double) aggs.get("max");

		Integer count = (Integer) aggs.get("count");

		// Obtener las fechas de los registros de cada uno de los sectores.
		DatesByDirectionListDTO datesByDirectionListDTO = getDatesByDirectionAggs(query, numSectors,
				directionDataDefinition);

		// Hacer query para obtener los datos en formato windrose
		WindroseDataDTO windroseDataDTO = getWindroseData(query, datesByDirectionListDTO, speedDataDefinition, min, max,
				count, partitionNumber);

		return new ElasticSearchDTO(windroseDataDTO, windroseDataDTO.getData().size());
	}

	private void checkValidNumSectors(Integer numSectors) {
		// Comprueba que numSectors sea un número válido. An = 2^(n-1)

		double x = (Math.log(numSectors) / Math.log(2));
		if (numSectors < 2 || x > 5 || (x != (int) x))
			throw new ESTermQueryException("numSectors", numSectors.toString());
	}

	private void checkValidPartitionNumber(Integer partitionNumber) {

		if (partitionNumber > PARTITION_NUMBER_LIMIT || partitionNumber < 1)
			throw new ESTermQueryException("partitionNumber", partitionNumber.toString());
	}

	@SuppressWarnings({ "unchecked" })
	private Map<String, Object> getStatAggs(DataQueryDTO query, Integer speedDataDefinition) {

		Aggregations statAggs = repository.getStatAggs(query, speedDataDefinition);

		return (Map<String, Object>) statAggs.getAttributes().get("value");
	}

	private DatesByDirectionListDTO getDatesByDirectionAggs(DataQueryDTO query, Integer numSectors,
			Integer directionDataDefinition) {

		Aggregations datesByDirectionAggs = repository.getDatesByDirectionAggs(query, numSectors,
				directionDataDefinition);

		return orikaMapper.getMapperFacade().convert(datesByDirectionAggs, DatesByDirectionListDTO.class, null);
	}

	@SuppressWarnings({ "serial" })
	private WindroseDataDTO getWindroseData(DataQueryDTO query, DatesByDirectionListDTO datesByDirectionListDTO,
			Integer speedDataDefinition, Double min, Double max, Integer count, Integer partitionNumber) {

		WindroseDataDTO windroseDataDTO = new WindroseDataDTO();

		query.getAggs().clear();
		query.addAgg(new AggsPropertiesDTO("windrose"));

		List<LimitsDTO> limits = getDataLimits(min, max, partitionNumber);

		windroseDataDTO.setLimits(limits);

		query.getTerms().put("limits", limits);

		query.getTerms().put("dataDefinition", new ArrayList<Integer>() {
			{
				add(speedDataDefinition);
			}
		});

		List<SeriesSearchWrapper<?>> results = repository.getWindroseData(query, datesByDirectionListDTO, count);

		// para cada respuesta obtenemos los resultados de la agregación
		for (int i = 0; i < results.size(); i++) {
			WindroseSectorDTO dataSector = orikaMapper.getMapperFacade().convert(results.get(i).getAggregations(),
					WindroseSectorDTO.class, null);
			dataSector.calculateValue(count);
			windroseDataDTO.addSectorData(dataSector);
		}
		return windroseDataDTO;
	}

	private List<LimitsDTO> getDataLimits(Double min, Double max, Integer partitionNumber) {

		if (min == null || max == null || (min > max) || min == max || partitionNumber == null || partitionNumber == 0)
			throw new ESTermQueryException("partitionNumber", partitionNumber.toString());

		double partitionLength = (max - min) / partitionNumber;

		List<LimitsDTO> limits = new ArrayList<LimitsDTO>();

		double limit = min;
		for (int i = 0; i < partitionNumber; i++) {
			limits.add(new LimitsDTO(limit, limit + partitionLength));
			limit += partitionLength;
		}

		return limits;
	}

	public RawDataDTO getSourceToResult(SeriesSearchWrapper<TimeSeries> result, List<Integer> dataDefinitionIds) {

		RawDataDTO data = new RawDataDTO();

		if (result.getTotal() == 0)
			return data;

		int size = result.getTotal();

		// Pide todos los dataDefinition de la consulta y comprueba que cumplen
		// las restricciones.
		Map<Long, DataDefinition> dataDefinitions = DataDefinitionUtils.getDataDefinitions(orikaMapper,
				dataDefinitionIds, geoFixedTimeSeriesESSevice);

		TimeSeriesDTO item = orikaMapper.getMapperFacade().map(result.getSource(0), TimeSeriesDTO.class);
		Long dataDefinitionId = item.getDataDefinition();
		data.setItemData(orikaMapper.getMapperFacade().map(item, RawDataItemDTO.class));

		DateTime currentDateTime = item.getDate();

		for (int i = 1; i < size; i++) {
			item = orikaMapper.getMapperFacade().map(result.getSource(i), TimeSeriesDTO.class);

			RawDataItemDTO newItem = DataDefinitionUtils.getItemData(orikaMapper, item, currentDateTime,
					dataDefinitionId, dataDefinitions);

			if (newItem != null) {
				data.setItemData(newItem);

				if (newItem.getValue() == null) // Si se inserta un valor nulo
												// intermedio, se inserta
												// también el item analizado
					data.setItemData(orikaMapper.getMapperFacade().map(item, RawDataItemDTO.class));
				currentDateTime = item.getDate();
				dataDefinitionId = item.getDataDefinition();
			}
		}
		data.setDataDefinitionIds(dataDefinitionIds);
		return data;
	}
}