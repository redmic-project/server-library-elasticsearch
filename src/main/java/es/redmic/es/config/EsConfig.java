package es.redmic.es.config;

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

import java.util.List;

public class EsConfig {
	
	private List<String> addresses;
	private Integer port;
	private String clusterName;
	private String xpackSecurityUser;
	
	public EsConfig() {
	}
	
	public EsConfig(List<String> addresses, Integer port, String clusterName, String xpackSecurityUser) {
		this.addresses = addresses;
		this.port = port;
		this.clusterName = clusterName;
		this.xpackSecurityUser = xpackSecurityUser;
	}
	
	public List<String> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	public String getXpackSecurityUser() {
		return xpackSecurityUser;
	}
	public void setXpackSecurityUser(String xpackSecurityUser) {
		this.xpackSecurityUser = xpackSecurityUser;
	}
}
