package es.redmic.es.geodata.tracking.platform.mapper;

import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.tracking.platform.dto.PlatformTrackingDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class PlatformTrackingESMapper extends CustomMapper<GeoPointData, PlatformTrackingDTO> {

	@Override
	public void mapBtoA(PlatformTrackingDTO b, GeoPointData a, MappingContext context) {

		if ( a.getProperties() != null) {
			
			String id = DataMapperUtils.convertIdentifier(b.getId(), DataPrefixType.PLATFORM_TRACKING);
			
			if (a.getProperties().getCollect() != null)
				a.getProperties().getCollect().setId(id);
			a.getProperties().getInTrack().setId(id);
			a.set_parentId(b.getProperties().getActivityId());
		}
	}
}
