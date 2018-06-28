package es.redmic.es.geodata.citation.mapper;

import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.geojson.citation.dto.CitationDTO;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class CitationESMapper extends CustomMapper<GeoPointData, CitationDTO> {

	@Override
	public void mapBtoA(CitationDTO b, GeoPointData a, MappingContext context) {

		if (a.getProperties() != null) {
			a.getProperties().getCollect().setId(DataMapperUtils.convertIdentifier(b.getId(), DataPrefixType.CITATION));
			a.set_parentId(b.getProperties().getActivityId());
		}
	}
}
