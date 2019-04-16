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
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.taxonomy.repository.TaxonESRepository;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.common.utils.HierarchicalUtils;
import ma.glasnost.orika.MappingContext;

@Component
public class TaxonESMapper extends TaxonBaseESMapper<Taxon, TaxonDTO> {

	@Autowired
	TaxonESRepository baseESRepository;

	@Override
	public void mapAtoB(Taxon a, TaxonDTO b, MappingContext context) {
		super.mapAtoB(a, b, context);

		if (a.getRank() != null) {
			if (a.getRank().getId() != 1)
				b.setParent(mapperFacade.map(getParent(a), TaxonDTO.class));
		}
	}

	@Override
	public void mapBtoA(TaxonDTO b, Taxon a, MappingContext context) {
		super.mapBtoA(b, a, context);

		a.setPath(getPath(b));
	}

	private Taxon getParent(Taxon taxon) {

		String parentId = HierarchicalUtils.getParentId(taxon.getPath());

		if (parentId == null)
			return null;

		return (Taxon) baseESRepository.findById(parentId).get_source();
	}

	private String getPath(TaxonDTO toIndex) {

		if (toIndex.getParent() == null)
			return "root" + "." + toIndex.getId();

		Taxon parent = (Taxon) baseESRepository.findById(Long.toString(toIndex.getParent().getId())).get_source();
		if (parent != null) {
			return parent.getPath() + "." + toIndex.getId();
		}
		return null;
	}
}
