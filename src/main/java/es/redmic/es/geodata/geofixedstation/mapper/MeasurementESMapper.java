package es.redmic.es.geodata.geofixedstation.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.parameter.service.ParameterESService;
import es.redmic.es.maintenance.parameter.service.UnitESService;
import es.redmic.models.es.geojson.properties.model.Measurement;
import es.redmic.models.es.maintenance.parameter.dto.UnitDTO;
import es.redmic.models.es.maintenance.parameter.model.ParameterBase;
import es.redmic.models.es.maintenance.parameter.model.Unit;
import es.redmic.models.es.series.common.dto.HierarchicalParameterDTO;
import es.redmic.models.es.series.common.dto.MeasurementDTO;
import es.redmic.models.es.series.common.model.HierarchicalParameterES;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class MeasurementESMapper extends CustomMapper<Measurement, MeasurementDTO> {

	@Autowired
	ParameterESService parameterESService;

	@Autowired
	UnitESService unitESService;

	@Override
	public void mapAtoB(Measurement a, MeasurementDTO b, MappingContext context) {

		b.setParameter(mapperFacade.map(a.getParameter(), HierarchicalParameterDTO.class));
		b.getParameter().setLeaves(1);
		b.setUnit(mapperFacade.map(a.getUnit(), UnitDTO.class));
	}

	@Override
	public void mapBtoA(MeasurementDTO b, Measurement a, MappingContext context) {

		ParameterBase parameterBase = (ParameterBase) mapperFacade.map(mapperFacade.newObject(b.getParameter(),
				DataMapperUtils.getBaseType(), DataMapperUtils.getObjectFactoryContext(parameterESService)),
				ParameterBase.class);

		a.setParameter(mapperFacade.map(parameterBase, HierarchicalParameterES.class));
		a.setUnit((Unit) mapperFacade.newObject(b.getUnit(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(unitESService)));
	}
}
