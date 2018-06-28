package es.redmic.es.administrative.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.ActivityRankESService;
import es.redmic.models.es.administrative.dto.ProgramDTO;
import es.redmic.models.es.administrative.model.Program;
import es.redmic.models.es.common.dto.DomainDTO;
import es.redmic.models.es.common.dto.DomainImplDTO;
import es.redmic.models.es.common.model.DomainES;
import ma.glasnost.orika.MappingContext;

@Component
public class ProgramESMapper extends ActivityBaseESMapper<Program, ProgramDTO> {
	
	@Autowired
	ActivityRankESService rankESService;
	
	@Override
	public void mapAtoB(Program a, ProgramDTO b, MappingContext context) {

		super.mapAtoB(a, b, context);
	}

	@Override
	public void mapBtoA(ProgramDTO b, Program a, MappingContext context) {
		
		DomainDTO rankDTO = new DomainImplDTO();
		rankDTO.setId(1L);
		a.setRank((DomainES) mapperFacade.newObject(rankDTO, DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(rankESService)));
		super.mapBtoA(b, a, context);
	}
}
