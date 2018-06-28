package es.redmic.es.geodata.infrastructure.mapper;

import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.infrastructure.dto.InfrastructureDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class InfrastructureESMapper extends CustomMapper<GeoPointData, InfrastructureDTO> {

	@Override
	public void mapBtoA(InfrastructureDTO b, GeoPointData a, MappingContext context) {
		
		if (a.getProperties() != null) {
			a.getProperties().getSite().setId(DataMapperUtils.convertIdentifier(b.getId(), DataPrefixType.INFRASTRUCTURE));
			a.set_parentId(b.getProperties().getActivityId());
		}
	}
}
