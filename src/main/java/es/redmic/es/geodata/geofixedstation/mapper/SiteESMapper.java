package es.redmic.es.geodata.geofixedstation.mapper;

import org.springframework.stereotype.Component;

import es.redmic.models.es.geojson.properties.model.Site;
import es.redmic.models.es.maintenance.survey.dto.FixedSurveyDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class SiteESMapper extends CustomMapper<Site, FixedSurveyDTO> {
	
	@Override
	public void mapAtoB(Site a, FixedSurveyDTO b, MappingContext context) {
		
		b.setLeaves(1);
	}
	
	@Override
	public void mapBtoA(FixedSurveyDTO b, Site a, MappingContext context) {		
		
		a.setPath("r."+context.getProperty("uuid"));
	}
}
