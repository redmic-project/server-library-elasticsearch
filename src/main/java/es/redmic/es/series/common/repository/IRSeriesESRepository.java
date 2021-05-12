package es.redmic.es.series.common.repository;

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

import org.elasticsearch.search.builder.SearchSourceBuilder;

import es.redmic.es.common.repository.IRBaseESRepository;
import es.redmic.models.es.common.model.BaseAbstractES;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.series.common.model.SeriesHitWrapper;
import es.redmic.models.es.series.common.model.SeriesHitsWrapper;
import es.redmic.models.es.series.common.model.SeriesSearchWrapper;

public interface IRSeriesESRepository<TModel extends BaseAbstractES> extends IRBaseESRepository<TModel> {

	public SeriesHitWrapper<?> findById(String id, String parentId, String grandparentId);
	public SeriesSearchWrapper<?> searchByIds(String[] ids);
	public SeriesHitsWrapper<?> mget(MgetDTO dto, String parentId, String grandparentId);
	public SeriesSearchWrapper<?> find(GeoDataQueryDTO queryDTO, String parentId, String grandparentId);
	public SeriesSearchWrapper<?> find(GeoDataQueryDTO queryDTO);
	public List<SeriesSearchWrapper<?>> multiFind(List<SearchSourceBuilder> searchs);
	public SeriesHitWrapper<?> findById(String id);
	public SeriesHitsWrapper<?> mget(MgetDTO dto);
}
