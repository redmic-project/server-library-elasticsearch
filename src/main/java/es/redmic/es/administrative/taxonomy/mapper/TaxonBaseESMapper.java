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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.StatusESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.TaxonRankESService;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonWithOutParentDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.administrative.taxonomy.model.TaxonAncestorsCompact;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.taxonomy.dto.RankDTO;
import es.redmic.models.es.maintenance.taxonomy.dto.StatusDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

public abstract class TaxonBaseESMapper<TModel extends Taxon, TDTO extends TaxonWithOutParentDTO>
		extends CustomMapper<TModel, TDTO> {

	@Autowired
	@Qualifier("TaxonServiceES")
	TaxonESService baseESService;

	@Autowired
	StatusESService statusESService;

	@Autowired
	TaxonRankESService taxonRankESService;

	@Override
	public void mapAtoB(TModel a, TDTO b, MappingContext context) {

		if (a.getRank() != null)
			b.setRank(mapperFacade.map(a.getRank(), RankDTO.class));

		if (a.getStatus() != null)
			b.setStatus(mapperFacade.map(a.getStatus(), StatusDTO.class));

		if (a.getValidAs() != null)
			b.setValidAs(mapperFacade.map(a.getValidAs(), TaxonDTO.class));
	}

	@Override
	public void mapBtoA(TDTO b, TModel a, MappingContext context) {

		a.setRank((DomainES) mapperFacade.newObject(b.getRank(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(taxonRankESService)));

		if (b.getStatus() != null)
			a.setStatus((DomainES) mapperFacade.newObject(b.getStatus(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(statusESService)));

		if (b.getValidAs() != null)
			a.setValidAs(mapperFacade.map(mapperFacade.newObject(b.getValidAs(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(baseESService)), TaxonAncestorsCompact.class));
	}
}
