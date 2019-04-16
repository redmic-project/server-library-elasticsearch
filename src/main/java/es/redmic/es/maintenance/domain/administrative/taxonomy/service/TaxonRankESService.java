package es.redmic.es.maintenance.domain.administrative.taxonomy.service;

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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.TaxonRankESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.SortDTO;
import es.redmic.models.es.maintenance.taxonomy.dto.RankDTO;

@Service
public class TaxonRankESService extends DomainESService<DomainES, RankDTO> {

	@Autowired
	@Qualifier("TaxonServiceES")
	TaxonESService taxonESService;

	@Autowired
	public TaxonRankESService(TaxonRankESRepository repository) {
		super(repository);
	}

	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		taxonESService.updateTaxonRank(reference);
	}

	@SuppressWarnings("unchecked")
	public List<RankDTO> getRankClassification() {

		DataQueryDTO query = new DataQueryDTO();
		SortDTO sort = new SortDTO();
		sort.setField("id");
		sort.setOrder("ASC");
		query.setSorts(Arrays.asList(sort));

		return find(query).getData();
	}
}
