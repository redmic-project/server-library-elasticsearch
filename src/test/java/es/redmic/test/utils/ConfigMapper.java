package es.redmic.test.utils;

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

public class ConfigMapper {

	private String dataIn;
	private String dataOut;
	private Class<?> outClass;
	private Class<?> inClass;
	private String geoDataPrefix;

	public ConfigMapper() {

	}

	public String getDataIn() {
		return dataIn;
	}

	public ConfigMapper setDataIn(String dataIn) {
		this.dataIn = dataIn;
		return this;
	}

	public String getDataOut() {
		return dataOut;
	}

	public ConfigMapper setDataOut(String dataOut) {
		this.dataOut = dataOut;
		return this;
	}

	public Class<?> getOutClass() {
		return outClass;
	}

	public ConfigMapper setOutClass(Class<?> outClass) {
		this.outClass = outClass;
		return this;
	}

	public Class<?> getInClass() {
		return inClass;
	}

	public ConfigMapper setInClass(Class<?> inClass) {
		this.inClass = inClass;
		return this;
	}

	public String getGeoDataPrefix() {
		return geoDataPrefix;
	}

	public ConfigMapper setGeoDataPrefix(String geoDataPrefix) {
		this.geoDataPrefix = geoDataPrefix;
		return this;
	}
}
