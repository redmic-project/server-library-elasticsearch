package es.redmic.es.administrative.taxonomy.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.animal.service.DestinyESService;
import es.redmic.es.maintenance.animal.service.EndingESService;
import es.redmic.models.es.administrative.taxonomy.dto.RecoveryDTO;
import es.redmic.models.es.administrative.taxonomy.model.Recovery;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.animal.dto.DestinyDTO;
import es.redmic.models.es.maintenance.animal.dto.EndingDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class RecoveryESMapper extends CustomMapper<Recovery, RecoveryDTO> {

	@Autowired
	EndingESService endingESService;
	
	@Autowired
	DestinyESService destinyESService;

	@Override
	public void mapAtoB(Recovery a, RecoveryDTO b, MappingContext context) {

		b.setEnding(mapperFacade.map(a.getEnding(), EndingDTO.class));
		b.setDestiny(mapperFacade.map(a.getDestiny(), DestinyDTO.class));
	}

	@Override
	public void mapBtoA(RecoveryDTO b, Recovery a, MappingContext context) {
		
		a.setEnding((DomainES) mapperFacade.newObject(b.getEnding(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(endingESService)));
		
		a.setDestiny((DomainES) mapperFacade.newObject(b.getDestiny(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(destinyESService)));
	}
}