package es.redmic.es.geodata.isolines.service;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.geodata.common.service.GeoDataWithSeriesESService;
import es.redmic.es.geodata.isolines.repository.IsolinesESRepository;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.geojson.common.model.GeoMultiLineStringData;
import es.redmic.models.es.geojson.isolines.dto.IsolinesDTO;

@Service
public class IsolinesESService extends GeoDataWithSeriesESService<IsolinesDTO, GeoMultiLineStringData> {
	
	@Autowired
	public IsolinesESService(IsolinesESRepository repository) {
		super(repository);
	}
	
	@Override
	public GeoMultiLineStringData mapper(IsolinesDTO dtoToIndex) {
		
		return orikaMapper.getMapperFacade().map(dtoToIndex, GeoMultiLineStringData.class);
	}

	@Override
	protected void postUpdate(ReferencesES<GeoMultiLineStringData> reference) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void postSave(Object model) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void preDelete(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void postDelete(String id) {
		// TODO Auto-generated method stub
		
	}
}
