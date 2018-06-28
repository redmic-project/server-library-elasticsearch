package es.redmic.es.geodata.area.mapper;

import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.geojson.area.dto.AreaDTO;
import es.redmic.models.es.geojson.common.model.GeoMultiPolygonData;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class AreaESMapper extends CustomMapper<GeoMultiPolygonData, AreaDTO> {

	@Override
	public void mapBtoA(AreaDTO b, GeoMultiPolygonData a, MappingContext context) {

		if (a.getProperties() != null) {
			a.getProperties().getSamplingPlace()
					.setId(DataMapperUtils.convertIdentifier(b.getId(), DataPrefixType.AREA));
			a.set_parentId(b.getProperties().getActivityId());
		}
	}
}
