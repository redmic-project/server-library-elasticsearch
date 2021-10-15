package es.redmic.test.unit.postupdate.config;

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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.model.BaseAbstractES;
import es.redmic.models.es.common.model.ReferencesES;

public class ReferencesConfig<TModel extends BaseAbstractES> {

	protected ObjectMapper jacksonMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JtsModule());

	private Class<?> serviceClass;
	private Class<?> repositoryClass;
	private Class<?> modelClass;

	private String dataInPath;
	private String dataInModifiedPath;

	private ReferencesES<TModel> referencesUtil;

	private List<DependenceConfig> dependences;

	public ReferencesConfig() {
		setDependences(new ArrayList<DependenceConfig>());
	}

	public Class<?> getServiceClass() {
		return serviceClass;
	}

	public ReferencesConfig<TModel> setServiceClass(Class<?> serviceClass) {
		this.serviceClass = serviceClass;
		return this;
	}

	public Class<?> getRepositoryClass() {
		return repositoryClass;
	}

	public ReferencesConfig<TModel> setRepositoryClass(Class<?> repositoryClass) {
		this.repositoryClass = repositoryClass;
		return this;
	}

	public Class<?> getModelClass() {
		return modelClass;
	}

	public ReferencesConfig<TModel> setModelClass(Class<?> modelClass) {
		this.modelClass = modelClass;
		return this;
	}

	public String getDataPath() {
		return dataInPath;
	}

	public ReferencesConfig<TModel> setDataInPath(String dataPath) {
		this.dataInPath = dataPath;
		if (getDataInModifiedPath() != null)
			this.setReferencesUtil(getReferences(getDataPath(), getDataInModifiedPath()));
		return this;
	}

	public String getDataInModifiedPath() {
		return dataInModifiedPath;
	}

	public ReferencesConfig<TModel> setDataInModifiedPath(String dataPathModified) {
		this.dataInModifiedPath = dataPathModified;
		if (getDataPath() != null)
			this.setReferencesUtil(getReferences(getDataPath(), getDataInModifiedPath()));
		return this;
	}

	public ReferencesES<TModel> getReferencesUtil() {
		return referencesUtil;
	}

	public ReferencesConfig<TModel> setReferencesUtil(ReferencesES<TModel> referencesUtil) {
		this.referencesUtil = referencesUtil;
		return this;
	}

	public List<DependenceConfig> getDependences() {
		return dependences;
	}

	public ReferencesConfig<TModel> setDependences(List<DependenceConfig> dependences) {
		this.dependences = dependences;
		return this;
	}

	public ReferencesConfig<TModel> addDependence(String serviceName, Class<?> serviceClass, Class<?> repositoryClass,
			Class<?> clazz, String path) {
		return addDependence(serviceName, serviceClass, repositoryClass, clazz, path, 1, false);
	}

	public ReferencesConfig<TModel> addDependence(String serviceName, Class<?> serviceClass, Class<?> repositoryClass,
			Class<?> clazz, String path, Integer nestingDepth, Boolean nestedProperty) {

		Object repository = Mockito.mock(repositoryClass);
		Object service;
		try {
			service = serviceClass.getDeclaredConstructor(repositoryClass).newInstance(repository);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION, e);
		}

		this.dependences.add(new DependenceConfig(serviceName, service, repository, repositoryClass, clazz, path,
				nestingDepth, nestedProperty));
		return this;
	}

	public ReferencesConfig<TModel> addDependence(String serviceName, Class<?> serviceClass, Class<?> repositoryClass,
			Class<?> clazz, String[] paths, Integer[] nestingDepths, Boolean[] nestedProperties) {

		Object repository = Mockito.mock(repositoryClass);
		Object service;
		try {
			service = serviceClass.getDeclaredConstructor(repositoryClass).newInstance(repository);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION, e);
		}

		if (paths == null || nestingDepths == null || nestedProperties == null || paths.length != nestingDepths.length
				|| paths.length != nestedProperties.length)
			return this;

		for (int i = 0; i < paths.length; i++)
			this.dependences.add(new DependenceConfig(serviceName, service, repository, repositoryClass, clazz,
					paths[i], nestingDepths[i], nestedProperties[i]));
		return this;
	}

	protected Object getBean(String filePath, Class<?> clazz) throws IOException {

		return jacksonMapper.readValue(getClass().getResource(filePath).openStream(), clazz);
	}

	@SuppressWarnings("unchecked")
	public ReferencesES<TModel> getReferences(String modelData, String modelDataModified) {

		try {
			return (ReferencesES<TModel>) new ReferencesES<TModel>((TModel) getBean(modelData, getModelClass()),
					(TModel) getBean(modelDataModified, getModelClass()));
		} catch (IOException e) {
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION, e);
		}
	}
}
