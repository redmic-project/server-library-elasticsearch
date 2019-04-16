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

import org.springframework.beans.factory.annotation.Autowired;

import es.redmic.es.common.repository.SettingsRepository;
import es.redmic.exception.security.NotAllowedException;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.dto.SettingsDTO;
import es.redmic.models.es.common.model.Settings;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public class SettingsServices<TModel extends Settings, TDTO extends SettingsDTO> extends RBaseESService<TModel, TDTO>
		implements ISettingsService<TModel, TDTO> {

	protected SettingsRepository<TModel, TDTO> repository;

	@Autowired(required = false)
	protected UserUtilsServiceItfc userService;

	public SettingsServices() {
	}

	public SettingsServices(SettingsRepository<TModel, TDTO> repository) {
		super();
		this.repository = repository;
	}

	public JSONCollectionDTO findAll(DataQueryDTO dto, String service) {

		if (userService == null) {
			LOGGER.debug("Imposible obtener la configuración. Usuario no válido");
			throw new NotAllowedException();

		}
		String userId = String.valueOf(userService.getUserId());
		DataSearchWrapper<?> result = repository.findByUserAndSearch(userId, service, dto);
		return orikaMapper.getMapperFacade().map(result.getHits(), JSONCollectionDTO.class, getMappingContext());
	}

	@Override
	public TDTO get(String id) {

		return orikaMapper.getMapperFacade().map(repository.findById(id), typeOfTDTO, getMappingContext());
	}

	@Override
	public List<String> suggest(DataQueryDTO queryDTO) {

		return repository.suggest(queryDTO);
	}

	@Override
	public void delete(String id) {

		repository.delete(id);
	}

	@Override
	public TModel save(TDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TModel update(TDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

	public SimpleQueryDTO createSimpleQueryDTOFromSuggestQueryParams(String[] fields, String text, Integer size) {
		return repository.createSimpleQueryDTOFromSuggestQueryParams(fields, text, size);
	}
}
