package es.redmic.es.maintenance.point.repository;

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

import es.redmic.es.maintenance.classification.repository.ClassificationBaseESRepository;
import es.redmic.models.es.maintenance.point.dto.InfrastructureTypeDTO;
import es.redmic.models.es.maintenance.point.model.InfrastructureType;

@Repository
public class InfrastructureTypeESRepository extends ClassificationBaseESRepository<InfrastructureType, InfrastructureTypeDTO> {

	private static String[] INDEX = { "classification-domains" };
	private static String[] TYPE = { "infrastructuretype" };
	
	public InfrastructureTypeESRepository() {
		super(INDEX, TYPE);
	}
}
