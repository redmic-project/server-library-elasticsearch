package es.redmic.es.geodata.tracking.common.service;

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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.service.PlatformESService;
import es.redmic.es.administrative.taxonomy.service.AnimalESService;
import es.redmic.es.config.OrikaScanBeanESItfc;
import es.redmic.es.geodata.common.service.RGeoDataESService;
import es.redmic.es.geodata.tracking.common.repository.TrackingESRepository;
import es.redmic.models.es.common.dto.AggregationsDTO;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.dto.UuidDTO;
import es.redmic.models.es.common.query.dto.AggsPropertiesDTO;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;
import es.redmic.models.es.geojson.tracking.common.ElementListDTO;
import es.redmic.models.es.geojson.tracking.common.ElementTrackingDTO;
import ma.glasnost.orika.MappingContext;

@Service
public class TrackingESService extends RGeoDataESService<ElementTrackingDTO, GeoPointData> {

	@Autowired
	protected OrikaScanBeanESItfc orikaMapper;

	TrackingESRepository repository;

	@Autowired
	AnimalESService animalESService;

	@Autowired
	PlatformESService platformESService;

	@Autowired
	public TrackingESService(TrackingESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	/*
	 * Devuelve el elemento buscado independientemente de si es animal o plataforma
	 */

	public UuidDTO getElement(String elementUuid) {

		UuidDTO animal = animalESService.findByUuid(elementUuid);

		if (animal != null)
			return orikaMapper.getMapperFacade().map(animal, UuidDTO.class);

		return orikaMapper.getMapperFacade().map(platformESService.findByUuid(elementUuid), UuidDTO.class);
	}

	/*
	 * Obtiene animales y plataformas relacionada con el track de una actividad
	 * dada.
	 */

	public JSONCollectionDTO getElementsByActivity(String activityId, GeoDataQueryDTO query) {

		query.setSize(0);
		// añade identificador para que se cree la agregación correspondinte en
		// el repositorio
		query.addAgg(new AggsPropertiesDTO("elements"));

		GeoSearchWrapper<?, ?> response = repository.find(query, activityId);

		// Obtenemos List de data en las agregaciones
		ElementListDTO dtoOut = orikaMapper.getMapperFacade().convert(response.getAggregations(), ElementListDTO.class,
				null, null);

		JSONCollectionDTO collection = new JSONCollectionDTO();
		collection.setData(dtoOut);
		collection.setTotal(dtoOut.size());
		return collection;
	}

	public GeoJSONFeatureCollectionDTO getTrackingPointsInLineStringCluster(String activityId, GeoDataQueryDTO queryDTO,
			String uuid) {

		GeoJSONFeatureCollectionDTO clusterCollection = repository.getTrackingPointsInLineStringCluster(activityId,
				queryDTO, uuid);

		return clusterCollection;
	}

	public GeoJSONFeatureCollectionDTO getTrackingPointsInLineStringCluster(String activityId, GeoDataQueryDTO queryDTO) {

		GeoJSONFeatureCollectionDTO clusterCollection = repository.getTrackingPointsInLineStringCluster(activityId,
				queryDTO);

		return clusterCollection;
	}

	/*
	 * Devuelve todos los puntos de un track para un elemento dado
	 */
	public GeoJSONFeatureCollectionDTO find(String activityId, String uuid, GeoDataQueryDTO queryDTO) {

		GeoSearchWrapper<?, ?> result = repository.find(activityId, uuid, queryDTO);

		if (result.getTotal() == 0)
			return new GeoJSONFeatureCollectionDTO();

		GeoJSONFeatureCollectionDTO featureCollection = orikaMapper.getMapperFacade().map(result.getHits(),
				GeoJSONFeatureCollectionDTO.class, getMappingContext());
		if (result.getAggregations() != null)
			featureCollection
					.set_aggs(orikaMapper.getMapperFacade().map(result.getAggregations(), AggregationsDTO.class));
		return featureCollection;
	}

	@Override
	protected MappingContext getMappingContext() {

		Map<Object, Object> globalProperties = new HashMap<>();
		globalProperties.put("targetTypeDto", ElementTrackingDTO.class);

		return new MappingContext(globalProperties);
	}
}
