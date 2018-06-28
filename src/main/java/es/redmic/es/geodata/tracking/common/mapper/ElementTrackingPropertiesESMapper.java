package es.redmic.es.geodata.tracking.common.mapper;

import org.springframework.stereotype.Component;

import es.redmic.models.es.administrative.dto.PlatformCompactDTO;
import es.redmic.models.es.administrative.taxonomy.dto.AnimalCompactDTO;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.tracking.common.ElementTrackingPropertiesDTO;
import ma.glasnost.orika.MappingContext;

@Component
public class ElementTrackingPropertiesESMapper extends TrackingPropertiesESMapper<GeoDataProperties, ElementTrackingPropertiesDTO> {

	@Override
	public void mapAtoB(GeoDataProperties a, ElementTrackingPropertiesDTO b, MappingContext context) {

		super.mapAtoB(a, b, context);

		if (a.getInTrack().getPlatform() != null)
			b.setElement(mapperFacade.map(a.getInTrack().getPlatform(), PlatformCompactDTO.class));
		else if (a.getCollect() != null && a.getCollect().getAnimal() != null) {
			b.setElement(mapperFacade.map(a.getCollect().getAnimal(), AnimalCompactDTO.class));
			((AnimalCompactDTO) b.getElement()).setTaxon(mapperFacade.map(a.getCollect().getTaxon(), TaxonDTO.class));
		}
	}
}
