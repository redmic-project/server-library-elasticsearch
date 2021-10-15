package es.redmic.es.administrative.taxonomy.repository;

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

import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.administrative.taxonomy.model.Misidentification;

@Repository
public class MisidentificationESRepository extends RWDataESRepository<Misidentification> {

	private static String[] INDEX = { "misidentification" };
	private static String TYPE = "_doc";

	public MisidentificationESRepository() {
		super(INDEX, TYPE);
	}

	@Override
	protected String getMappingFilePath(String index, String type) {
		return MAPPING_BASE_PATH + "taxon/" + getIndex()[0] + MAPPING_FILE_EXTENSION;
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "taxon.scientificName.suggest", "badIdentity.scientificName.suggest",
				"document.title.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "taxon.scientificName.suggest", "badIdentity.scientificName.suggest",
				"document.title.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "taxon.scientificName", "badIdentity.scientificName", "document.title" };
	}
}
