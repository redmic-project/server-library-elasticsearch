package es.redmic.es.common.repository;

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

import org.elasticsearch.search.SearchHit;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.tracking.common.TrackingClustersDTO;


public class ProcessClusterFunction implements IProcessItemFunction<GeoHitWrapper<GeoDataProperties, Point>> {

	TrackingClustersDTO items;
	int zoomLevel;

	protected ObjectMapper objectMapper;


	public ProcessClusterFunction(ObjectMapper objectMapper, int zoomLevel) {
		this.objectMapper = objectMapper;
		this.zoomLevel = zoomLevel;
		items = new TrackingClustersDTO(zoomLevel);
	}

	@Override
	public void process(SearchHit hit) {
		GeoHitWrapper<GeoDataProperties, Point> item = mapper(hit);
		items.addFeature(item);
	}

	private GeoHitWrapper<GeoDataProperties, Point> mapper(SearchHit hit) {
		GeoPointData data = objectMapper.convertValue(hit.getSourceAsMap(), GeoPointData.class);
		GeoHitWrapper<GeoDataProperties, Point> item = new GeoHitWrapper<GeoDataProperties, Point>();
		item.set_source(data);
		item.set_id(hit.getId());
		item.set_version(hit.getVersion());

		return item;
	}

	@Override
	public List<?> getResults() {
		return items;
	}

}
