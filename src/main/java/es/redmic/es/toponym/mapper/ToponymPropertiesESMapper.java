package es.redmic.es.toponym.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.toponym.service.ToponymTypeESService;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.geojson.toponym.dto.ToponymPropertiesDTO;
import es.redmic.models.es.geojson.toponym.model.ToponymProperties;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ToponymPropertiesESMapper extends CustomMapper<ToponymProperties, ToponymPropertiesDTO> {

	@Autowired
	public ToponymTypeESService toponymTypeESService;

	/*@Override
	public void mapAtoB(ToponymProperties a, ToponymPropertiesDTO b, MappingContext context) {

		b.setTaxon((TaxonDTO) mapperFacade.map(a.getCollect().getTaxon(), TaxonDTO.class));
	
	}*/

	@Override
	public void mapBtoA(ToponymPropertiesDTO b, ToponymProperties a, MappingContext context) {
		
		if (b.getToponymType() != null)
			a.setToponymType((DomainES) mapperFacade.newObject(b.getToponymType(),
					DataMapperUtils.getBaseType(), DataMapperUtils.getObjectFactoryContext(toponymTypeESService)));
	}
}
