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

public class DependenceConfig {

	private Object repository;

	private Object service;

	private String serviceName;

	private Class<?> repositoryClass;
	private Class<?> modelClass;
	private String path;

	/* Multilevel */
	private Integer nestingDepth = 1;
	private Boolean nestedProperty = false;

	/*public DependenceConfig(String serviceName, RWElasticSearchService<?, ?> service,
			RWElasticSearchRepository<?, ?> repository, Class<?> modelClass, String path) {

		this.setServiceName(serviceName)
			.setService(service)
			.setRepository(repository)
			.setModelClass(modelClass)
			.setPath(path);
	}*/

	public DependenceConfig(String serviceName, Object service,
			Object repository, Class<?> repositoryClass, Class<?> modelClass, String path,
			Integer nestingDepth, Boolean nestedProperty) {

		this.setServiceName(serviceName)
			.setService(service)
			.setRepository(repository)
			.setRepositoryClass(repositoryClass)
			.setModelClass(modelClass)
			.setPath(path)
			.setNestingDepth(nestingDepth)
			.setNestedProperty(nestedProperty);
	}

	public Object getRepository() {
		return repository;
	}

	public DependenceConfig setRepository(Object repository) {
		this.repository = repository;
		return this;
	}

	public Object getService() {
		return service;
	}

	public DependenceConfig setService(Object service) {
		this.service = service;
		return this;
	}

	public String getServiceName() {
		return serviceName;
	}

	public DependenceConfig setServiceName(String serviceName) {
		this.serviceName = serviceName;
		return this;
	}

	public Class<?> getRepositoryClass() {
		return repositoryClass;
	}

	public DependenceConfig setRepositoryClass(Class<?> repositoryClass) {
		this.repositoryClass = repositoryClass;
		return this;
	}

	public Class<?> getModelClass() {
		return modelClass;
	}

	public DependenceConfig setModelClass(Class<?> modelClass) {
		this.modelClass = modelClass;
		return this;
	}

	public String getPath() {
		return path;
	}

	public DependenceConfig setPath(String path) {
		this.path = path;
		return this;
	}

	public Integer getNestingDepth() {
		return nestingDepth;
	}

	public DependenceConfig setNestingDepth(Integer nestingDepth) {
		this.nestingDepth = nestingDepth;
		return this;
	}

	public Boolean getNestedProperty() {
		return nestedProperty;
	}

	public DependenceConfig setNestedProperty(Boolean nestedProperty) {
		this.nestedProperty = nestedProperty;
		return this;
	}
}
