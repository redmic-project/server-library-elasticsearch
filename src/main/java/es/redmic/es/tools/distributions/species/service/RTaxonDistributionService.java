package es.redmic.es.tools.distributions.species.service;

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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.es.common.repository.SelectionWorkRepository;
import es.redmic.es.geodata.common.service.GridServiceItfc;
import es.redmic.es.tools.distributions.species.repository.RTaxonDistributionRepository;
import es.redmic.exception.elasticsearch.ESTooManySelectedItemsException;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.models.es.tools.distribution.dto.TaxonDistributionRegistersDTO;

public abstract class RTaxonDistributionService {

	@Autowired
	TaxonDistributionUtils taxonDistributionUtils;

	@Value("${redmic.elasticsearch.MAX_QUERY_SIZE}")
	Integer maxQuerySize;

	protected char qFlagHigh = '1';

	GridServiceItfc gridUtil;

	RTaxonDistributionRepository repository;

	String className = "Distribution";

	@Autowired
	SelectionWorkRepository selectionWorkRepository;

	@Autowired
	ObjectMapper objectMapper;

	public RTaxonDistributionService(RTaxonDistributionRepository repository, GridServiceItfc gridUtil) {
		this.repository = repository;
		this.gridUtil = gridUtil;
	}

	public GeoJSONFeatureCollectionDTO findAll(DataQueryDTO queryDTO) {

		List<String> ids = getIdsBySelection(queryDTO);

		if (ids == null)
			return new GeoJSONFeatureCollectionDTO();

		return repository.findAll(queryDTO, ids);
	}

	public List<TaxonDistributionRegistersDTO> findByGridIdAndTaxons(String gridId, DataQueryDTO queryDTO) {

		List<String> taxonsIds = getIdsBySelection(queryDTO);

		if (taxonsIds == null || taxonsIds.size() <= 0)
			return new ArrayList<TaxonDistributionRegistersDTO>();

		return repository.findByGridIdAndTaxons(queryDTO, gridId, taxonsIds);
	}

	private List<String> getIdsBySelection(DataQueryDTO queryDTO) {

		List<String> ids = null;

		if (queryDTO != null && queryDTO.getTerms() != null && queryDTO.getTerms().get("selection") != null) {
			ids = selectionWorkRepository.getSelectedIds(queryDTO.getTerms().get("selection").toString());
		}

		if ((ids == null || ids.size() <= 0)
				&& (queryDTO.getTerms() == null && queryDTO.getTerms().get("taxonId") == null))
			return null;

		if (ids != null && ids.size() > maxQuerySize)
			throw new ESTooManySelectedItemsException();

		return ids;
	}
}
