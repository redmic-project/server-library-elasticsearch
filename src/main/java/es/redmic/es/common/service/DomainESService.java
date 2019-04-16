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

import java.util.List;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.es.data.common.service.RWDataESService;
import es.redmic.models.es.common.dto.DTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public abstract class DomainESService<TModel extends DomainES, TDTO extends DTO> extends RWDataESService<TModel, TDTO> {
	DomainESRepository<TModel> repository;

	public DomainESService(DomainESRepository<TModel> repository) {
		super(repository);
		this.repository = repository;
	}

	@SuppressWarnings("unchecked")
	public DomainES findByName(String name) {

		DataSearchWrapper<DomainES> registers = (DataSearchWrapper<DomainES>) repository.findByName(name);
		List<DomainES> sourceList = registers.getSourceList();
		if (sourceList.size() == 0)
			return null;

		return sourceList.get(0);
	}

	@SuppressWarnings("unchecked")
	public DomainES findByName_en(String name) {

		DataSearchWrapper<DomainES> registers = (DataSearchWrapper<DomainES>) repository.findByName_en(name);
		List<DomainES> sourceList = registers.getSourceList();
		if (sourceList.size() == 0)
			return null;

		return sourceList.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TModel mapper(TDTO dtoToIndex) {
		return (TModel) orikaMapper.getMapperFacade().map(dtoToIndex, DomainES.class);
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
