package es.redmic.es.administrative.taxonomy.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.animal.service.LifeStageESService;
import es.redmic.es.maintenance.animal.service.SexESService;
import es.redmic.models.es.administrative.taxonomy.dto.AnimalDTO;
import es.redmic.models.es.administrative.taxonomy.dto.RecoveryDTO;
import es.redmic.models.es.administrative.taxonomy.dto.SpecimenTagDTO;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.Animal;
import es.redmic.models.es.administrative.taxonomy.model.Recovery;
import es.redmic.models.es.administrative.taxonomy.model.SpecimenTag;
import es.redmic.models.es.administrative.taxonomy.model.TaxonValid;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.animal.dto.LifeStageDTO;
import es.redmic.models.es.maintenance.animal.dto.SexDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class AnimalESMapper extends CustomMapper<Animal, AnimalDTO> {

	@Autowired
	SexESService sexESService;

	@Autowired
	LifeStageESService lifeStageESService;
	
	@Autowired
	@Qualifier("TaxonServiceES")
	TaxonESService taxonESService;

	@Override
	public void mapAtoB(Animal a, AnimalDTO b, MappingContext context) {

		b.setLifeStage(mapperFacade.map(a.getLifeStage(), LifeStageDTO.class));
		b.setSex(mapperFacade.map(a.getSex(), SexDTO.class));
		b.setTaxon(mapperFacade.map(a.getTaxon(), TaxonDTO.class));
		if (a.getRecoveries() != null)
			b.setRecoveries(mapperFacade.mapAsList(a.getRecoveries(), RecoveryDTO.class));
		if (a.getSpecimenTags() != null)
			b.setSpecimenTags(mapperFacade.mapAsList(a.getSpecimenTags(), SpecimenTagDTO.class));
	}

	@Override
	public void mapBtoA(AnimalDTO b, Animal a, MappingContext context) {
		
		a.setLifeStage((DomainES) mapperFacade.newObject(b.getLifeStage(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(lifeStageESService)));
		a.setSex((DomainES) mapperFacade.newObject(b.getSex(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(sexESService)));
		a.setTaxon(mapperFacade.map(mapperFacade.newObject(b.getTaxon(), DataMapperUtils.getBaseType(),
			DataMapperUtils.getObjectFactoryContext(taxonESService)), TaxonValid.class));
		if (b.getRecoveries() != null)
			a.setRecoveries(mapperFacade.mapAsList(b.getRecoveries(), Recovery.class));
		if (b.getSpecimenTags() != null)
			a.setSpecimenTags(mapperFacade.mapAsList(b.getSpecimenTags(), SpecimenTag.class));
	}
}