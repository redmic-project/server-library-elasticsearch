package es.redmic.es.data.common.service;

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
import es.redmic.models.es.common.dto.BaseDTO;
import es.redmic.models.es.common.model.BaseES;

public abstract class RWUserDataESService<TModel extends BaseES<?>, TDTO extends BaseDTO<?>>
		extends RDataESService<TModel, TDTO> {

	private RWDataESRepository<TModel> repository;

	protected static String DELETE_EVENT = "DELETE";
	protected static String ADD_EVENT = "ADD";
	protected static String UPDATE_EVENT = "UPDATE";

	public RWUserDataESService(RWDataESRepository<TModel> repository) {
		super(repository);
		this.repository = repository;
	}

	public TModel save(TModel modelToIndex) {

		return repository.save(modelToIndex);
	}

	public TModel update(TModel modelToIndex) {

		TModel origin = findById(modelToIndex.getId().toString());
		if (origin != null) {
			return repository.update(modelToIndex);
		} else
			return save(modelToIndex);
	}

	public void delete(String id) {

		repository.delete(id);
	}
}
