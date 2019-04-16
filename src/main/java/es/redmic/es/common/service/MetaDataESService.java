package es.redmic.es.common.service;

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

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.es.data.common.service.RWDataESService;
import es.redmic.models.es.common.dto.BaseDTO;
import es.redmic.models.es.common.model.BaseES;

public abstract class MetaDataESService<TModel extends BaseES<?>, TDTO extends BaseDTO<?>>
		extends RWDataESService<TModel, TDTO> {

	RWDataESRepository<TModel> repository;

	public MetaDataESService(RWDataESRepository<TModel> repository) {
		super(repository);
		this.repository = repository;
	}

	@Override
	public TModel mapper(TDTO dtoToIndex) {
		return orikaMapper.getMapperFacade().map(dtoToIndex, typeOfTModel);
	}

	@Override
	protected void postSave(Object model) {
	}

	@Override
	protected void preDelete(Object object) {
	}

	@Override
	protected void postDelete(String id) {
	}
}
