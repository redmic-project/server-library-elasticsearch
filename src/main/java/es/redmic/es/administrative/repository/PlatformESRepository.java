package es.redmic.es.administrative.repository;

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

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.administrative.dto.PlatformDTO;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Repository
public class PlatformESRepository extends RWDataESRepository<Platform> {

	private static String[] INDEX = { "platform" };
	private static String TYPE = "administrative";

	public PlatformESRepository() {
		super(INDEX, TYPE);
	}

	@SuppressWarnings("unchecked")
	public PlatformDTO findByUuid(String uuid) {

		QueryBuilder query = QueryBuilders.termQuery("uuid", uuid);
		DataSearchWrapper<Platform> result = (DataSearchWrapper<Platform>) findBy(query);
		if (result == null)
			return null;
		return orikaMapper.getMapperFacade().map(result.getSource(0), PlatformDTO.class);
	}
}
