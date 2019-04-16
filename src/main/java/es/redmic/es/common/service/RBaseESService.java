package es.redmic.es.common.service;

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

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

import es.redmic.es.config.OrikaScanBeanESItfc;
import es.redmic.models.es.common.dto.BaseDTO;
import es.redmic.models.es.common.dto.ElasticSearchDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.FilterSchemaDTO;
import es.redmic.models.es.common.view.JsonViewsForQueryDTO;
import es.redmic.models.es.common.view.JsonViewsForQueryDTO.ViewClassInterface;
import es.redmic.models.es.utils.JacksonFieldUtils;
import ma.glasnost.orika.MappingContext;

public abstract class RBaseESService<TModel extends BaseES<?>, TDTO extends BaseDTO<?>> {

	@Autowired
	protected OrikaScanBeanESItfc orikaMapper;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	UserUtilsServiceItfc userService;

	protected Class<TDTO> typeOfTDTO;

	protected Class<TModel> typeOfTModel;

	protected Map<Object, Object> globalProperties = new HashMap<Object, Object>();

	protected final Logger LOGGER = LoggerFactory.getLogger(RBaseESService.class);

	@SuppressWarnings("unchecked")
	public RBaseESService() {

		this.typeOfTModel = (Class<TModel>) (((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0]);

		this.typeOfTDTO = (Class<TDTO>) (((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[1]);
	}

	@SuppressWarnings("unchecked")
	public ElasticSearchDTO getFilterSchema(Class<?> queryDTOClass, Set<String> ignorableFieldNames) {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JodaModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		Class<?> jsonView = getJsonView();

		Set<String> fieldsToFilter = new HashSet<String>();
		Set<String> fieldsNoIncludedInView = JacksonFieldUtils.getFieldNamesNoIncludedInView(queryDTOClass,
				(Class<ViewClassInterface>) jsonView);

		if (fieldsNoIncludedInView != null && fieldsNoIncludedInView.size() > 0)
			fieldsToFilter.addAll(fieldsNoIncludedInView);

		if (ignorableFieldNames != null)
			fieldsToFilter.addAll(ignorableFieldNames);

		FilterProvider filters = new SimpleFilterProvider().setFailOnUnknownId(false).addFilter("DataQueryDTO",
				SimpleBeanPropertyFilter.serializeAllExcept(fieldsToFilter));
		mapper.setFilterProvider(filters);

		JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(mapper);
		JsonNode jsonSchema = jsonSchemaGenerator.generateJsonSchema(queryDTOClass);

		FilterSchemaDTO dto = new FilterSchemaDTO();
		dto.setSchema(objectMapper.convertValue(jsonSchema, Map.class));

		return new ElasticSearchDTO(dto, 1);
	}

	public Class<?> getJsonView() {
		return JsonViewsForQueryDTO.getJsonView(userService.getUserRole());
	}

	protected MappingContext getMappingContext() {

		globalProperties.put("targetTypeDto", typeOfTDTO);
		return new MappingContext(globalProperties);
	}
}
