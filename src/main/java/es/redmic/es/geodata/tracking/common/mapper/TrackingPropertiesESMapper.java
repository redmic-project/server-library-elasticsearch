package es.redmic.es.geodata.tracking.common.mapper;

import org.springframework.beans.factory.annotation.Autowired;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.device.service.DeviceESService;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.properties.model.InTrack;
import es.redmic.models.es.geojson.tracking.common.BaseTrackingPropertiesDTO;
import es.redmic.models.es.maintenance.device.dto.DeviceCompactDTO;
import es.redmic.models.es.maintenance.device.model.DeviceCompact;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

public abstract class TrackingPropertiesESMapper<TProperties extends GeoDataProperties, DTO extends BaseTrackingPropertiesDTO>
		extends CustomMapper<TProperties, DTO> {

	@Autowired
	DeviceESService deviceService;
	
	public TrackingPropertiesESMapper() {
		super();
	}
	
	@Override
	public void mapAtoB(TProperties a, DTO b, MappingContext context) {

		b.setArgosId(a.getInTrack().getArgosId());
		b.setSpeedKph(a.getInTrack().getSpeedKph());
		b.setHours(a.getInTrack().getHours());
		b.setLastDistanceKm(a.getInTrack().getLastDistanceKm());
		b.setPassDuration(a.getInTrack().getPassDuration());
		b.setCumulativeTime(a.getInTrack().getCumulativeTime());
		b.setCumulativeKm(a.getInTrack().getCumulativeKm());
		b.setLocationClass(a.getInTrack().getLocationClass());

		b.setZ(a.getInTrack().getZ());

		b.setvFlag(a.getInTrack().getvFlag());
		b.setqFlag(a.getInTrack().getqFlag());

		b.setDate(a.getInTrack().getDate());
		b.setUpdated(a.getUpdated());

		b.setDevice(mapperFacade.map(a.getInTrack().getDevice(), DeviceCompactDTO.class));
	}

	@Override
	public void mapBtoA(DTO b, TProperties a, MappingContext context) {

		InTrack inTrack = new InTrack();

		inTrack.setZ(b.getZ());
		inTrack.setDeviation(b.getDeviation());
		inTrack.setArgosId(b.getArgosId());
		inTrack.setSpeedKph(b.getSpeedKph());
		inTrack.setHours(b.getHours());
		inTrack.setLastDistanceKm(b.getLastDistanceKm());
		inTrack.setPassDuration(b.getPassDuration());
		inTrack.setCumulativeTime(b.getCumulativeTime());
		inTrack.setCumulativeKm(b.getCumulativeKm());
		inTrack.setLocationClass(b.getLocationClass());
		inTrack.setvFlag(b.getvFlag());
		inTrack.setqFlag(b.getqFlag());
		inTrack.setDate(b.getDate());
		inTrack.setDevice(
				(DeviceCompact) mapperFacade.map(mapperFacade.newObject(b.getDevice(), DataMapperUtils.getBaseType(),
						DataMapperUtils.getObjectFactoryContext(deviceService)), DeviceCompact.class));

		a.setInTrack(inTrack);
		a.setUpdated(b.getUpdated());
	}
}
