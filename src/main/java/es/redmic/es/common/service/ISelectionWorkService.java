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

import es.redmic.es.data.common.service.RDataESService;
import es.redmic.models.es.common.dto.SelectionBaseDTO;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

public interface ISelectionWorkService<TDTO extends SelectionBaseDTO> {

	public TDTO get(String id);

	public TDTO save(TDTO dto, RDataESService<?, ?> service);

	public TDTO update(TDTO dto, RDataESService<?, ?> service);

	public List<String> getAllIds(DataQueryDTO query, RDataESService<?, ?> service, String idProperty);
}
