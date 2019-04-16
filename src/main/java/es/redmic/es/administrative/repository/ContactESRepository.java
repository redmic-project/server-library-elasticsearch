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

import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.administrative.model.Contact;

@Repository
public class ContactESRepository extends RWDataESRepository<Contact> {

	private static String[] INDEX = { "administrative" };
	private static String[] TYPE = { "contact" };

	public ContactESRepository() {
		super(INDEX, TYPE);
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "firstName", "firstName.suggest", "surname", "surname.suggest", "fullname.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "firstName", "firstName.suggest", "surname", "surname.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "firstName", "surname", "fullname" };
	}
}
