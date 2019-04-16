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

import java.util.List;

import es.redmic.models.es.common.dto.BaseDTO;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;

public interface IRDataESService<TModel extends BaseES<?>, TDTO extends BaseDTO<?>> {

	public TDTO get(String id);
	public TModel findById(String id);
	public TDTO searchById(String id);
	public JSONCollectionDTO find(DataQueryDTO query);
	public List<String> suggest(DataQueryDTO queryDTO);
	public JSONCollectionDTO mget(MgetDTO dto);
}
