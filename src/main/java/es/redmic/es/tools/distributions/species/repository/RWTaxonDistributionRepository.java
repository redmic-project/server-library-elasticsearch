package es.redmic.es.tools.distributions.species.repository;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.ElasticPersistenceUtils;
import es.redmic.exception.elasticsearch.ESUpdateException;
import es.redmic.models.es.tools.distribution.model.Distribution;
import es.redmic.models.es.tools.distribution.species.model.TaxonDistribution;
import es.redmic.models.es.tools.distribution.species.model.TaxonProperties;

@Component
public class RWTaxonDistributionRepository extends RTaxonDistributionRepository {

	public static String CITATION_PATH = "/citation/";
	public static String ANIMAL_TRACKING_PATH = "/animaltracking/";
	public static String CITATION_BASE_URL = "/activity/{id}" + CITATION_PATH;
	public static String ANIMAL_TRACKING_BASE_URL = "/activity/{id}" + ANIMAL_TRACKING_PATH;
	public static String ID_STR_REPLACE = "{id}";

	@Autowired
	ElasticPersistenceUtils<Distribution> elasticPersistenceUtils;

	public RWTaxonDistributionRepository() {
	}

	public RWTaxonDistributionRepository(String[] INDEX, String[] TYPE) {
		super(INDEX, TYPE);
	}

	// TODO: Igual que RW -> si se puede refactorizar
	@SuppressWarnings("unchecked")
	public IndexResponse save(Distribution toIndex) {

		String id = String.valueOf(toIndex.getId());

		return ESProvider.getClient().prepareIndex(INDEX[0], TYPE[0], id).setRefreshPolicy(RefreshPolicy.IMMEDIATE)
				.setSource(objectMapper.convertValue(toIndex, Map.class)).execute().actionGet();
	}

	@SuppressWarnings({ "unchecked" })
	public void addRegister(TaxonDistribution dist, Distribution distributionProperties, String script) {

		List<UpdateRequest> requestBuilder = new ArrayList<UpdateRequest>();

		Map<String, Object> fields = new HashMap<String, Object>();

		String gridId = String.valueOf(distributionProperties.getId());
		GetResponse response = findById(gridId);
		// Si existe el grid pero no el registro se añade
		if (response != null && response.isExists()) {
			fields.put("item", (Map<String, Object>) objectMapper.convertValue(dist, Map.class));
			try {
				requestBuilder.addAll(elasticPersistenceUtils.getUpdateScript(INDEX, TYPE, gridId, fields, script));
			} catch (Exception e) {
				throw new ESUpdateException(e);
			}
		} else { // Si no existe el grid se crea junto con el registro
			TaxonProperties tProps = new TaxonProperties();
			tProps.setTaxons(new ArrayList<TaxonDistribution>(Arrays.asList(dist)));
			distributionProperties.setProperties(tProps);
			save(distributionProperties);
		}

		if (requestBuilder.size() > 0)
			elasticPersistenceUtils.updateByBulk(requestBuilder);
	}

	@SuppressWarnings("unchecked")
	public void updateRegister(String id, TaxonDistribution dist, String script) {

		List<UpdateRequest> requestBuilder = new ArrayList<UpdateRequest>();

		Map<String, Object> fields = new HashMap<String, Object>();

		fields.put("item", (Map<String, Object>) objectMapper.convertValue(dist, Map.class));
		requestBuilder.addAll(elasticPersistenceUtils.getUpdateScript(INDEX, TYPE, id, fields, script));

		if (requestBuilder.size() > 0)
			elasticPersistenceUtils.updateByBulk(requestBuilder);
	}

	public void deleteRegister(String id, String script) {

		List<UpdateRequest> requestBuilder = new ArrayList<UpdateRequest>();

		Map<String, Object> fields = new HashMap<String, Object>();
		fields.put("item_id", id);

		TaxonDistribution source = findByRegisterId(id);
		if (source != null) {
			try {
				requestBuilder.addAll(elasticPersistenceUtils.getUpdateScript(INDEX, TYPE,
						String.valueOf(source.getId()), fields, script));
			} catch (Exception e) {
				throw new ESUpdateException(e);
			}
		}

		if (requestBuilder.size() > 0)
			elasticPersistenceUtils.updateByBulk(requestBuilder);
	}
}
