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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;

import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;

public class EsClientProvider {

	private TransportClient client;

	private List<String> addresses;
	private Integer port;
	private String clusterName;
	private String xpackSecurityUser;
	
	protected static Logger logger = LogManager.getLogger();

	public EsClientProvider(EsConfig config) {
		this.addresses = config.getAddresses();
		this.port = config.getPort();
		this.clusterName = config.getClusterName();
		this.xpackSecurityUser = config.getXpackSecurityUser();
	}

	public TransportClient getClient() {
		if (client == null)
			connect();
		return client;
	}

	@PostConstruct
	private void connect() {

		Settings settings = Settings.builder()
		    .put("cluster.name", this.clusterName)
		    .put("xpack.security.user", this.xpackSecurityUser)
		    .build();

		client = new PreBuiltXPackTransportClient(settings);

		for (String address : addresses) {
			try {
				client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(address), port));
			} catch (UnknownHostException e) {
				logger.warn(e.getMessage());
			}
		}
		
		List<DiscoveryNode> nodes = client.connectedNodes();
		if (nodes == null || nodes.isEmpty()) {
			// TODO: Añadir excepción propia
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
	}

	@PreDestroy
	private void disconnect() {
		client.close();
	}

}
