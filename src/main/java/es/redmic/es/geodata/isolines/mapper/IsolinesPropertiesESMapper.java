package es.redmic.es.geodata.isolines.mapper;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.parameter.service.ParameterESService;
import es.redmic.es.maintenance.parameter.service.UnitESService;
import es.redmic.models.es.geojson.common.model.InnerHits;
import es.redmic.models.es.geojson.isolines.dto.IsolinesPropertiesDTO;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.properties.model.Measurement;
import es.redmic.models.es.geojson.properties.model.SamplingPlace;
import es.redmic.models.es.maintenance.common.model.ClassificationItem;
import es.redmic.models.es.maintenance.line.dto.LineTypeBaseDTO;
import es.redmic.models.es.maintenance.parameter.model.ParameterBase;
import es.redmic.models.es.maintenance.parameter.model.Unit;
import es.redmic.models.es.series.common.model.HierarchicalParameterES;
import es.redmic.models.es.series.common.model.SeriesHitWrapper;
import es.redmic.models.es.series.timeseries.model.TimeSeries;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;

@Component
public class IsolinesPropertiesESMapper extends CustomMapper<GeoDataProperties, IsolinesPropertiesDTO> {

	@Autowired
	ParameterESService parameterESService;

	@Autowired
	UnitESService unitESService;

	final Type<LineTypeBaseDTO> lineTypeBaseDTOType = new TypeBuilder<LineTypeBaseDTO>() {
	}.build();
	final Type<List<ClassificationItem>> classificationListType = new TypeBuilder<List<ClassificationItem>>() {
	}.build();

	@Override
	public void mapAtoB(GeoDataProperties a, IsolinesPropertiesDTO b, MappingContext context) {

		if (a.getSamplingPlace().getClassification() != null)
			b.setLineType(mapperFacade.convert(a.getSamplingPlace().getClassification(), classificationListType,
					lineTypeBaseDTOType, null));

		if (a.getMeasurements().size() == 1) {
			Measurement measurement = a.getMeasurements().get(0);
			mapperFacade.map(measurement, b);

			InnerHits inner_hits = (InnerHits) context.getProperty("inner_hits");

			if (inner_hits != null && inner_hits.getTimeseries() != null
					&& inner_hits.getTimeseries().getHits().getHits().size() == 1) {
				SeriesHitWrapper<TimeSeries> timeSeriesItem = inner_hits.getTimeseries().getHits().getHits().get(0);
				Long dataDefId = measurement.getDataDefinition().getId();
				TimeSeries timeSeries = timeSeriesItem.get_source();

				if (timeSeries.getDataDefinition().equals(dataDefId)) {

					b.setValue(timeSeries.getValue());
					b.setQFlag(timeSeries.getQFlag());
					b.setVFlag(timeSeries.getVFlag());
				}
			}
		}
	}

	@Override
	public void mapBtoA(IsolinesPropertiesDTO b, GeoDataProperties a, MappingContext context) {

		if (a.getSamplingPlace() == null)
			a.setSamplingPlace(new SamplingPlace());

		a.getSamplingPlace().setClassification(
				mapperFacade.convert(b.getLineType(), lineTypeBaseDTOType, classificationListType, null));

		Measurement measurement = mapperFacade.map(b, Measurement.class);

		ParameterBase parameterBase = mapperFacade.map(mapperFacade.newObject(b.getParameter(),
				DataMapperUtils.getBaseType(), DataMapperUtils.getObjectFactoryContext(parameterESService)),
				ParameterBase.class);

		measurement.setParameter(mapperFacade.map(parameterBase, HierarchicalParameterES.class));
		measurement.setUnit((Unit) mapperFacade.newObject(b.getUnit(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(unitESService)));

		a.setMeasurements(Arrays.asList(measurement));
	}
}
