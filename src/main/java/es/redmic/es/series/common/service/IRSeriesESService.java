package es.redmic.es.series.common.service;

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

import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.series.common.dto.SeriesCommonDTO;
import es.redmic.models.es.series.common.model.SeriesCommon;

public interface IRSeriesESService<TModel extends SeriesCommon, TDTO extends SeriesCommonDTO> {

	public TDTO get(String id, String parentId, String grandparentId);
	public TModel findById(String id, String parentId, String grandparentId);
	public TDTO searchById(String id);
	public JSONCollectionDTO find(DataQueryDTO query);
	public JSONCollectionDTO find(DataQueryDTO query, String parentId, String grandparentId);
	public JSONCollectionDTO mget(MgetDTO dto, String parentId, String grandparentId);
}
