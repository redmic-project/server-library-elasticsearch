package es.redmic.es.maintenance.domain.administrative.service;

/*-
 * #%L
 * ElasticSearch
 * %%
 * Copyright (C) 2019 - 2021 REDMIC Project / Server
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

import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.maintenance.domain.administrative.repository.ThemeInspireESRepository;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.administrative.dto.ThemeInspireDTO;
import es.redmic.models.es.maintenance.administrative.model.ThemeInspire;

@Service
public class ThemeInspireESService extends MetaDataESService<ThemeInspire, ThemeInspireDTO> {

	@Autowired
	public ThemeInspireESService(ThemeInspireESRepository repository) {
		super(repository);
	}

	@Override
	protected void postUpdate(ReferencesES<ThemeInspire> reference) {
		// TODO Auto-generated method stub
	}
}
