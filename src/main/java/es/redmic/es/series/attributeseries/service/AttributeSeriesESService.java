package es.redmic.es.series.attributeseries.service;

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
import org.springframework.stereotype.Service;

import es.redmic.es.series.attributeseries.repository.AttributeSeriesESRepository;
import es.redmic.es.series.common.service.RWSeriesESService;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.series.attributeseries.dto.AttributeSeriesDTO;
import es.redmic.models.es.series.attributeseries.model.AttributeSeries;

@Service
public class AttributeSeriesESService extends RWSeriesESService<AttributeSeries, AttributeSeriesDTO> {

	@Autowired
	public AttributeSeriesESService(AttributeSeriesESRepository repository) {
		super(repository);
	}

	@Override
	public AttributeSeries mapper(AttributeSeriesDTO dtoToIndex) {
		return orikaMapper.getMapperFacade().map(dtoToIndex, AttributeSeries.class);
	}

	@Override
	protected void postUpdate(ReferencesES<AttributeSeries> reference) {}

	@Override
	protected void postSave(Object model) {}

	@Override
	protected void preDelete(Object object) {}

	@Override
	protected void postDelete(String id) {}
}
