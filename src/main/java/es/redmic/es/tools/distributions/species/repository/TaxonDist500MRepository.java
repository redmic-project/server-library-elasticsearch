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

import org.springframework.stereotype.Repository;

@Repository
public class TaxonDist500MRepository extends RWTaxonDistributionRepository {

	private static String[] INDEX = { "distribution-500" };
	private static String TYPE = "_doc";

	private Integer gridSize = 500;

	public TaxonDist500MRepository() {
		super(INDEX, TYPE);
	}

	@Override
	public Integer getGridSize() {
		return gridSize;
	}
}
