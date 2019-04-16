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

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.search.SearchHit;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Point;

import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.common.utils.GeometryUtils;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.tracking.common.BaseTrackingClusterDTO;
import es.redmic.models.es.geojson.tracking.common.TrackingClusterDTO;
import es.redmic.models.es.geojson.tracking.common.linestring.TrackingLinestringClusterDTO;

public class ProcessClusterMultiElementFunction
		implements IProcessItemFunction<GeoHitWrapper<GeoDataProperties, Point>> {

	int zoomLevel;
	String elementUuid;

	private static String CRS_NAME = "EPSG:4326";
	private static int MIN_PIXELS_TO_CLUSTER = 8;

	private CoordinateReferenceSystem crs = GeometryUtils.getCRS(CRS_NAME);

	List<BaseTrackingClusterDTO> clustersByElements = new ArrayList<BaseTrackingClusterDTO>();

	protected ObjectMapper objectMapper;

	public ProcessClusterMultiElementFunction(ObjectMapper objectMapper, int zoomLevel) {
		this.objectMapper = objectMapper;
		this.zoomLevel = zoomLevel;
	}

	@Override
	public void process(SearchHit hit) {
		proccesFeature(mapper(hit));
	}
	
	private void proccesFeature(GeoPointData feature) {
		String uuid = getElementUuid(feature);
		if (!uuid.equals(elementUuid)) {
			clustersByElements.add(new TrackingClusterDTO(feature));
			elementUuid = uuid;
		} else {
			BaseTrackingClusterDTO cluster = clustersByElements.get(clustersByElements.size() - 1);

			if (checkPointBelongsToCluster(cluster.getCentroid(), feature.getGeometry(), zoomLevel))
				cluster.addPoinInCluster(feature);
			else if (TrackingLinestringClusterDTO.class.isInstance(cluster)) {
				((TrackingLinestringClusterDTO) cluster).addAxis(feature);
			} else if (TrackingClusterDTO.class.isInstance(cluster)) {
				TrackingLinestringClusterDTO linestringCluster = new TrackingLinestringClusterDTO(
						(TrackingClusterDTO) cluster, feature);
				clustersByElements.set(clustersByElements.size() - 1, linestringCluster);
			} else {
				// TODO Lanzar exception de tipo de instancia no encontrada
			}

		}
	}

	private GeoPointData mapper(SearchHit hit) {
		GeoPointData data = objectMapper.convertValue(hit.getSource(), GeoPointData.class);
		GeoHitWrapper<GeoDataProperties, Point> item = new GeoHitWrapper<GeoDataProperties, Point>();
		item.set_source(data);
		item.set_id(hit.getId());
		item.set_version(hit.getVersion());
		
		return (GeoPointData) item.get_source();
	}

	public boolean checkPointBelongsToCluster(Point cluster, Point point, int zoomLevel) {

		Double distanceInMeters = GeometryUtils.getDistanceInMeters(cluster.getCoordinate(), 
									point.getCoordinate(), crs);

		Double meterByPixel = GeometryUtils.getMeterByPixel(zoomLevel, point.getCoordinate().y);
		
		Double pixels = distanceInMeters / meterByPixel;

		return !(pixels > MIN_PIXELS_TO_CLUSTER);
	}

	@Override
	public List<?> getResults() {
		return clustersByElements;
	}
	
	private String getElementUuid(final GeoPointData feature) {

		String uuid = null;
		if (feature.getProperties().getInTrack().getPlatform() != null)
			uuid = feature.getProperties().getInTrack().getPlatform().getUuid();
		else if ((feature.getProperties().getCollect() != null)
				&& (feature.getProperties().getCollect().getAnimal() != null))
			uuid = feature.getProperties().getCollect().getAnimal().getUuid();

		return uuid;
	}

}
