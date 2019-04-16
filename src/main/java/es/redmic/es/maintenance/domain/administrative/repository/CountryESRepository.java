package es.redmic.es.maintenance.domain.administrative.repository;

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

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.maintenance.administrative.model.Country;

@Repository
public class CountryESRepository extends DomainESRepository<Country> {
	private static String[] INDEX = { "administrative-domains" };
	private static String[] TYPE = { "country" };

	public CountryESRepository() {
		super(INDEX, TYPE);
	}
	
	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "name", "name.suggest", "code", "code.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "name", "name.suggest", "code", "code.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "name", "code" };
	}
}
