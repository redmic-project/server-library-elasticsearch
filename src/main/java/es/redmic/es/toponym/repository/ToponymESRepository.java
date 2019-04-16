package es.redmic.es.toponym.repository;

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

import java.util.HashMap;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JavaType;
import com.vividsolutions.jts.geom.Geometry;

import es.redmic.es.geodata.common.repository.RWGeoDataESRepository;
import es.redmic.models.es.common.request.dto.CategoryPathInfo;
import es.redmic.models.es.geojson.toponym.model.Toponym;
import es.redmic.models.es.geojson.toponym.model.ToponymProperties;

@Repository
public class ToponymESRepository extends RWGeoDataESRepository<Toponym>{

	protected static String[] INDEX = { "toponym" };
	protected static String[] TYPE = { "toponym" };

	public ToponymESRepository() {
		super(INDEX, TYPE);
	}
	
	@Override
	protected JavaType getSourceType(Class<?> wrapperClass) {
		return objectMapper.getTypeFactory().constructParametricType(wrapperClass, ToponymProperties.class, Geometry.class);
	}
	
	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "properties.name", "properties.name.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "properties.name", "properties.name.suggest" };
	}
	
	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "properties.name" };
	}
	
	@Override
	public HashMap<String, CategoryPathInfo> getCategoriesPaths() {
		return null;
	}
}
