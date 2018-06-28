package es.redmic.es.series.attributeseries.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.qualifiers.service.AttributeTypeESService;
import es.redmic.models.es.maintenance.qualifiers.dto.AttributeTypeBaseDTO;
import es.redmic.models.es.maintenance.qualifiers.model.AttributeTypeBase;
import es.redmic.models.es.series.attributeseries.dto.AttributeSeriesDTO;
import es.redmic.models.es.series.attributeseries.model.AttributeSeries;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class AttributeSeriesESMapper extends CustomMapper<AttributeSeries, AttributeSeriesDTO> {
	
	@Autowired
	AttributeTypeESService attributeTypeESService;
	
	@Override
	public void mapAtoB(AttributeSeries a, AttributeSeriesDTO b, MappingContext context) {
		
		if (a.getAttributeType() != null)
			b.setAttributeType(mapperFacade.map(a.getAttributeType(), AttributeTypeBaseDTO.class));
	}
	
	@Override
	public void mapBtoA(AttributeSeriesDTO b, AttributeSeries a, MappingContext context) {
		
		if (b.getAttributeType() != null) {
			a.setAttributeType(mapperFacade.map(mapperFacade.newObject(b.getAttributeType(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(attributeTypeESService)), AttributeTypeBase.class));
		}
	}
}