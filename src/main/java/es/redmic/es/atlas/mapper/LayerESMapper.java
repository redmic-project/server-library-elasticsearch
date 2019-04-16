package es.redmic.es.atlas.mapper;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.repository.ActivityBaseESRepository;
import es.redmic.es.atlas.service.LayerESService;
import es.redmic.es.atlas.service.ThemeInspireESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.models.es.administrative.model.Activity;
import es.redmic.models.es.administrative.model.ActivityReferences;
import es.redmic.models.es.atlas.dto.LayerDTO;
import es.redmic.models.es.atlas.dto.ProtocolsDTO;
import es.redmic.models.es.atlas.dto.ThemeInspireDTO;
import es.redmic.models.es.atlas.model.LayerModel;
import es.redmic.models.es.atlas.model.Protocols;
import es.redmic.models.es.atlas.model.ThemeInspire;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.data.common.model.DataHitsWrapper;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class LayerESMapper extends CustomMapper<LayerModel, LayerDTO> {
	
	@Autowired
	LayerESService service;
	
	@Autowired
	ActivityBaseESRepository activityESRepository;
	
	@Autowired
	ThemeInspireESService themeInspireESService;

	@Override
	public void mapAtoB(LayerModel a, LayerDTO b, MappingContext context) {

		if (a.getParentId() != null)
			b.setParent(service.get(a.getParentId().toString()));
		
		if (a.getProtocols() != null)
			b.setProtocols(mapperFacade.mapAsList(a.getProtocols(), ProtocolsDTO.class));
		
		if (a.getThemeInspire() != null)
			b.setThemeInspire(mapperFacade.map(a.getThemeInspire(), ThemeInspireDTO.class));
	}

	@Override
	public void mapBtoA(LayerDTO b, LayerModel a, MappingContext context) {
		
		a = preparateModel(b, a);
		
		a.setPath(getPath(b));
		
		if (a.getLeaves() == null)
			a.setLeaves(0);

		if (b.getParent() != null)
			a.setParentId(b.getParent().getId());
		
		if (a.getProtocols() != null)
			a.setProtocols(mapperFacade.mapAsList(b.getProtocols(), Protocols.class));
		
		if (b.getThemeInspire() != null)
			a.setThemeInspire((ThemeInspire) mapperFacade.newObject(b.getThemeInspire(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(themeInspireESService)));
	}
	
	private String getPath(LayerDTO toIndex) {

		if (toIndex.getParent() == null)
			return "root" + "." + toIndex.getId();

		LayerDTO parent = service.get(toIndex.getParent().getId().toString());
		if (parent != null) {
			return parent.getPath() + "." + toIndex.getId();
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	private LayerModel preparateModel(LayerDTO b, LayerModel a) {

		if (b.getIdActivities() != null && b.getIdActivities().isEmpty() != true) {
					
			DataHitsWrapper<Activity> result = (DataHitsWrapper<Activity>) activityESRepository.mget(new MgetDTO(b.getIdActivities()));
			List<Activity> data = result.getSourceList();
			List<Activity> activities = mapperFacade.mapAsList(data, Activity.class);
			a.setActivities(mapperFacade.mapAsList(activities, ActivityReferences.class));
			if (b.getIdActivities().size() != a.getActivities().size()) {
				for (int i = 0; i < b.getIdActivities().size(); i++) {
					Boolean exist = false;
					for (int j = 0; j < a.getActivities().size(); j++)
						if (b.getIdActivities().get(i) == a.getActivities().get(j).getId().toString()) {
							exist = true;
							break;
						}
					if (exist == false)
						System.err.println("Save layer:" + b.getName() + ", no existe la actividad con ID = "
								+ b.getIdActivities().get(i));
				}
			}
		}

		return a;
	}
}
