package es.redmic.es.geodata.common.repository;

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

import es.redmic.es.common.repository.IRBaseESRepository;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.models.es.geojson.common.model.GeoHitsWrapper;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;

public interface IRGeoDataESRepository<TModel extends Feature<?, ?>> extends IRBaseESRepository<TModel> {

	public GeoHitWrapper<?, ?> findById(String id, String parentId);
	public GeoSearchWrapper<?, ?> searchByIds(String[] ids);
	public List<String> suggest(String parentId, DataQueryDTO queryDTO);
	public GeoHitsWrapper<?, ?> mget(MgetDTO dto, String parentId);
	public GeoSearchWrapper<?, ?> find(DataQueryDTO queryDTO, String parentId);
	public GeoSearchWrapper<?, ?> find(DataQueryDTO queryDTO);
	public GeoHitWrapper<?, ?> findById(String id);
	public GeoHitsWrapper<?, ?> mget(MgetDTO dto);
}
