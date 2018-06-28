package es.redmic.es.geodata.area.mapper;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.geodata.area.repository.AreaESRepository;
import es.redmic.es.maintenance.area.service.AreaTypeESService;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.geojson.area.dto.AreaPropertiesDTO;
import es.redmic.models.es.geojson.common.dto.GeoDataRelationDTO;
import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.properties.model.SamplingPlace;
import es.redmic.models.es.maintenance.areas.dto.AreaClassificationDTO;
import es.redmic.models.es.maintenance.areas.dto.AreaTypeDTO;
import es.redmic.models.es.maintenance.areas.model.AreaClassification;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class AreaPropertiesESMapper extends CustomMapper<GeoDataProperties, AreaPropertiesDTO> {

	@Autowired
	AreaTypeESService areaTypeESService;

	@Autowired
	AreaESRepository areaESRepository;

	@Override
	public void mapAtoB(GeoDataProperties a, AreaPropertiesDTO b, MappingContext context) {

		mapperFacade.map(a.getSamplingPlace(), b);

		if (a.getGeodataRelations() != null && a.getGeodataRelations().size() > 0) {

			GeoHitWrapper<?, ?> parent = areaESRepository.findBySamplingId(a.getGeodataRelations().get(0));
			
			GeoDataRelationDTO relationDTO = new GeoDataRelationDTO();
			relationDTO.setId(parent.get_source().getId());
			relationDTO.setActivityId(parent.get_parent());
			relationDTO.setUuid(parent.get_source().getUuid());
			
			b.setParent(relationDTO);
		}

		if (a.getSamplingPlace().getAreaType() != null) {
			b.setAreaType(mapperFacade.map(a.getSamplingPlace().getAreaType(), AreaTypeDTO.class));
		}

		if (a.getSamplingPlace().getAreaClassification() != null) {
			b.setAreaClassification(
					mapperFacade.mapAsList(a.getSamplingPlace().getAreaClassification(), AreaClassificationDTO.class));
		}
	}

	@Override
	public void mapBtoA(AreaPropertiesDTO b, GeoDataProperties a, MappingContext context) {

		a.setSamplingPlace(mapperFacade.map(b, SamplingPlace.class));

		if (b.getParent() != null) {
			a.setGeodataRelations(
					Arrays.asList(DataMapperUtils.convertIdentifier(b.getParent().getId(), DataPrefixType.AREA)));
		}

		if (b.getAreaType() != null) {
			a.getSamplingPlace()
					.setAreaType(mapperFacade.map(mapperFacade.newObject(b.getAreaType(), DataMapperUtils.getBaseType(),
							DataMapperUtils.getObjectFactoryContext(areaTypeESService)), DomainES.class));
		}

		if (b.getAreaClassification() != null) {
			a.getSamplingPlace()
					.setAreaClassification(mapperFacade.mapAsList(b.getAreaClassification(), AreaClassification.class));
		}
	}
}
