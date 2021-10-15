package es.redmic.es.maintenance.domain.administrative.repository;

import org.springframework.stereotype.Repository;

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

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.maintenance.administrative.model.ThemeInspire;

@Repository
public class ThemeInspireESRepository extends RWDataESRepository<ThemeInspire> {

	private static String[] INDEX = { "theme-inspire" };
	private static String TYPE = "_doc";

	public ThemeInspireESRepository() {
		super(INDEX, TYPE);
	}

	@Override
	protected String getMappingFilePath(String index, String type) {
		return MAPPING_BASE_PATH + "domains/" + INDEX[0] + MAPPING_FILE_EXTENSION;
	}
}
