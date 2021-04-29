package es.redmic.es.administrative.taxonomy.mapper;

/*-
 * #%L
 * ElasticSearch
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.service.DocumentESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.CanaryProtectionESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.EUProtectionESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.EcologyESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.EndemicityESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.InterestESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.OriginESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.PermanenceESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.SpainProtectionESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.TrophicRegimeESService;
import es.redmic.models.es.administrative.dto.DocumentDTO;
import es.redmic.models.es.administrative.model.DocumentCompact;
import es.redmic.models.es.administrative.taxonomy.dto.SpeciesDTO;
import es.redmic.models.es.administrative.taxonomy.model.Peculiarity;
import es.redmic.models.es.administrative.taxonomy.model.Species;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.taxonomy.dto.CanaryProtectionDTO;
import es.redmic.models.es.maintenance.taxonomy.dto.EUProtectionDTO;
import es.redmic.models.es.maintenance.taxonomy.dto.EcologyDTO;
import es.redmic.models.es.maintenance.taxonomy.dto.EndemicityDTO;
import es.redmic.models.es.maintenance.taxonomy.dto.InterestDTO;
import es.redmic.models.es.maintenance.taxonomy.dto.OriginDTO;
import es.redmic.models.es.maintenance.taxonomy.dto.PermanenceDTO;
import es.redmic.models.es.maintenance.taxonomy.dto.SpainProtectionDTO;
import es.redmic.models.es.maintenance.taxonomy.dto.TrophicRegimeDTO;
import ma.glasnost.orika.MappingContext;

@Component
public class SpeciesESMapper extends TaxonBaseESMapper<Species, SpeciesDTO> {

	@Autowired
	DocumentESService documentService;

	@Autowired
	CanaryProtectionESService canaryProtectionService;

	@Autowired
	EcologyESService ecologyService;

	@Autowired
	EndemicityESService endemicityService;

	@Autowired
	EUProtectionESService euProtectionService;

	@Autowired
	InterestESService interestService;

	@Autowired
	OriginESService originService;

	@Autowired
	PermanenceESService permanenceService;

	@Autowired
	SpainProtectionESService spainProtectionService;

	@Autowired
	TrophicRegimeESService trophicRegimeESService;

	@Override
	public void mapAtoB(Species a, SpeciesDTO b, MappingContext context) {

		super.mapAtoB(a, b, context);
		if (a.getPeculiarity().getCanaryCatalogue() != null)
			b.setCanaryCatalogue(mapperFacade.map(a.getPeculiarity().getCanaryCatalogue(), DocumentDTO.class));
		if (a.getPeculiarity().getEuDirective() != null)
			b.setEuDirective(mapperFacade.map(a.getPeculiarity().getEuDirective(), DocumentDTO.class));
		if (a.getPeculiarity().getSpainCatalogue() != null)
			b.setSpainCatalogue(mapperFacade.map(a.getPeculiarity().getSpainCatalogue(), DocumentDTO.class));
		if (a.getPeculiarity().getCanaryProtection() != null)
			b.setCanaryProtection(mapperFacade.map(a.getPeculiarity().getCanaryProtection(), CanaryProtectionDTO.class));
		if (a.getPeculiarity().getEcology() != null)
			b.setEcology(mapperFacade.map(a.getPeculiarity().getEcology(), EcologyDTO.class));
		if (a.getPeculiarity().getEndemicity() != null)
			b.setEndemicity(mapperFacade.map(a.getPeculiarity().getEndemicity(), EndemicityDTO.class));
		if (a.getPeculiarity().getEuProtection() != null)
			b.setEuProtection(mapperFacade.map(a.getPeculiarity().getEuProtection(), EUProtectionDTO.class));
		if (a.getPeculiarity().getInterest() != null)
			b.setInterest(mapperFacade.map(a.getPeculiarity().getInterest(), InterestDTO.class));
		if (a.getPeculiarity().getOrigin() != null)
			b.setOrigin(mapperFacade.map(a.getPeculiarity().getOrigin(), OriginDTO.class));
		if (a.getPeculiarity().getPermanence() != null)
			b.setPermanence(mapperFacade.map(a.getPeculiarity().getPermanence(), PermanenceDTO.class));
		if (a.getPeculiarity().getSpainProtection() != null)
			b.setSpainProtection(mapperFacade.map(a.getPeculiarity().getSpainProtection(), SpainProtectionDTO.class));
		if (a.getPeculiarity().getTrophicRegime() != null)
			b.setTrophicRegime(mapperFacade.map(a.getPeculiarity().getTrophicRegime(), TrophicRegimeDTO.class));

		if (a.getPeculiarity().getPopularNames() != null)
			b.setPopularNames(StringUtils.join(a.getPeculiarity().getPopularNames(), ','));
	}

	@Override
	public void mapBtoA(SpeciesDTO b, Species a, MappingContext context) {

		super.mapBtoA(b, a, context);
		Peculiarity peculiarity = new Peculiarity();

		if (b.getCanaryCatalogue() != null)
			peculiarity.setCanaryCatalogue(mapperFacade.map(mapperFacade.newObject(b.getCanaryCatalogue(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(documentService)), DocumentCompact.class));

		if (b.getEuDirective() != null)
			peculiarity.setEuDirective(mapperFacade.map(mapperFacade.newObject(b.getEuDirective(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(documentService)), DocumentCompact.class));

		if (b.getSpainCatalogue() != null)
			peculiarity.setSpainCatalogue(mapperFacade.map(mapperFacade.newObject(b.getSpainCatalogue(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(documentService)), DocumentCompact.class));

		if (b.getCanaryProtection() != null)
			peculiarity.setCanaryProtection((DomainES) mapperFacade.newObject(b.getCanaryProtection(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(canaryProtectionService)));
		if (b.getEcology() != null)
			peculiarity.setEcology((DomainES) mapperFacade.newObject(b.getEcology(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(ecologyService)));
		if (b.getEndemicity() != null)
			peculiarity.setEndemicity((DomainES) mapperFacade.newObject(b.getEndemicity(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(endemicityService)));
		if (b.getEuProtection() != null)
			peculiarity.setEuProtection((DomainES) mapperFacade.newObject(b.getEuProtection(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(euProtectionService)));
		if (b.getInterest() != null)
			peculiarity.setInterest((DomainES) mapperFacade.newObject(b.getInterest(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(interestService)));
		if (b.getOrigin() != null)
			peculiarity.setOrigin((DomainES) mapperFacade.newObject(b.getOrigin(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(originService)));
		if (b.getPermanence() != null)
			peculiarity.setPermanence((DomainES) mapperFacade.newObject(b.getPermanence(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(permanenceService)));
		if (b.getSpainProtection() != null)
			peculiarity.setSpainProtection((DomainES) mapperFacade.newObject(b.getSpainProtection(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(spainProtectionService)));
		if (b.getTrophicRegime() != null)
			peculiarity.setTrophicRegime((DomainES) mapperFacade.newObject(b.getTrophicRegime(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(trophicRegimeESService)));

		if (b.getPopularNames() != null) {
			String[] popularNamesSplit = b.getPopularNames().split(",");
			HashSet<String> sourcePopularNames = new HashSet<String>(Arrays.asList(popularNamesSplit));
			peculiarity.setPopularNames(sourcePopularNames);
		}

		a.setPeculiarity(peculiarity);
	}
}
