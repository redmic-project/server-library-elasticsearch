package es.redmic.es.geodata.tracking.platform.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.PlatformESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.geodata.tracking.common.mapper.TrackingPropertiesESMapper;
import es.redmic.models.es.administrative.dto.PlatformCompactDTO;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.administrative.model.PlatformCompact;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.tracking.platform.dto.PlatformTrackingPropertiesDTO;
import ma.glasnost.orika.MappingContext;

@Component
public class PlatformTrackingPropertiesESMapper extends TrackingPropertiesESMapper<GeoDataProperties, PlatformTrackingPropertiesDTO> {

	@Autowired
	PlatformESService platformESService;

	@Override
	public void mapAtoB(GeoDataProperties a, PlatformTrackingPropertiesDTO b, MappingContext context) {

		super.mapAtoB(a, b, context);
		
		b.setPlatform(mapperFacade.map(a.getInTrack().getPlatform(), PlatformCompactDTO.class));
	}

	@Override
	public void mapBtoA(PlatformTrackingPropertiesDTO b, GeoDataProperties a, MappingContext context) {

		super.mapBtoA(b, a, context);

		Platform platform = (Platform) mapperFacade.newObject(b.getPlatform(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(platformESService));
		
		a.getInTrack().setPlatform(mapperFacade.map(platform, PlatformCompact.class));
	}
}
