package es.redmic.test.unit.geodata.tracking.common;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import es.redmic.es.common.repository.ProcessClusterMultiElementFunction;
import es.redmic.exception.elasticsearch.ESParseException;
import es.redmic.models.es.administrative.model.PlatformCompact;
import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.models.es.geojson.common.model.GeoPointData;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.properties.model.InTrack;
import es.redmic.models.es.geojson.tracking.common.BaseTrackingClusterDTO;
import es.redmic.models.es.geojson.tracking.common.TrackingClusterDTO;
import es.redmic.models.es.geojson.tracking.common.linestring.TrackingLinestringClusterDTO;


public class ProcessClusterMultiElementFunctionTest {
	
	ObjectMapper objectMapper = new ObjectMapper();
	int zoomLevel = 19;

	private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);
	static GeometryFactory geomFactory = new GeometryFactory();
	static TrackingClusterDTO clusterDTO;
	
	static GeoPointData f1, f2, f3;
	static PlatformCompact pl1, pl2;	
	static Double x1, y1, x2, y2, x3, y3;

	@BeforeClass
	public static void setUp() {
		
		GeoHitWrapper<GeoDataProperties, Point> h1 = new GeoHitWrapper<GeoDataProperties, Point>();
		GeoHitWrapper<GeoDataProperties, Point> h2 = new GeoHitWrapper<GeoDataProperties, Point>();
		GeoHitWrapper<GeoDataProperties, Point> h3 = new GeoHitWrapper<GeoDataProperties, Point>();

		f1 = new GeoPointData();
		f2 = new GeoPointData();
		f3 = new GeoPointData();

		h1.set_source(f1);
		h2.set_source(f2);
		h3.set_source(f3);
		
		x1 = -14.86667d;
		y1 = 27.44999d;
		x2 = -14.26666d;
		y2 = 28.86669d;
		x3 = -13.48335d;
		y3 = 28.25000d;
		
		pl1 = new PlatformCompact();
		pl1.setId(1L);
		pl1.setUuid("1L");
		
		pl2 = new PlatformCompact();
		pl2.setId(2L);
		pl2.setUuid("2L");

		GeoDataProperties p1 = new GeoDataProperties();
		InTrack i1 = new InTrack();
		i1.setId("it-234");
		i1.setDate(getDateTime("2009-08-29T12:32:00.000+01:00"));
		p1.setInTrack(i1);
		
		GeoDataProperties p2 = new GeoDataProperties();
		InTrack i2 = new InTrack();
		i2.setId("it-256");
		i2.setDate(getDateTime("2009-08-29T13:32:00.000+01:00"));
		p2.setInTrack(i2);
		
		GeoDataProperties p3 = new GeoDataProperties();
		InTrack i3 = new InTrack();
		i3.setId("it-289");
		i3.setDate(getDateTime("2009-08-29T14:32:00.000+01:00"));
		p3.setInTrack(i3);
		
		Point point1 = geomFactory.createPoint(new Coordinate(x1, y1));
		Point point2 = geomFactory.createPoint(new Coordinate(x2, y2));
		Point point3 = geomFactory.createPoint(new Coordinate(x3, y3));
		
		f1.setGeometry(point1);
		f1.setProperties(p1);
		f1.setUuid("uuuid1");

		f2.setGeometry(point2);
		f2.setProperties(p2);
		f2.setUuid("uuuid2");

		f3.setGeometry(point3);
		f3.setProperties(p3);
		f3.setUuid("uuuid3");

		
	}

	@SuppressWarnings("unchecked")
	@Test
	public void should_returnListWithATrackingClusterDTO_when_processFirstPoint() throws Exception {
				
		ProcessClusterMultiElementFunction process = new ProcessClusterMultiElementFunction(objectMapper, zoomLevel);
		
		f1.getProperties().getInTrack().setPlatform(pl1);
		
		Whitebox.invokeMethod(process, "proccesFeature", f1);
		
		List<BaseTrackingClusterDTO> geometryCollection = (List<BaseTrackingClusterDTO>) process.getResults();
		
		assertEquals(geometryCollection.size(), 1);
		assertTrue(geometryCollection.get(0) instanceof TrackingClusterDTO);		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void should_returnListWithATrackingLinestringClusterDTO_when_processTwoPoint() throws Exception {
				
		ProcessClusterMultiElementFunction process = new ProcessClusterMultiElementFunction(objectMapper, zoomLevel);
		
		f1.getProperties().getInTrack().setPlatform(pl1);
		f2.getProperties().getInTrack().setPlatform(pl1);
		
		Whitebox.invokeMethod(process, "proccesFeature", f1);
		Whitebox.invokeMethod(process, "proccesFeature", f2);
		
		List<BaseTrackingClusterDTO> geometryCollection = (List<BaseTrackingClusterDTO>) process.getResults();
		
		assertEquals(geometryCollection.size(), 1);
		assertTrue(geometryCollection.get(0) instanceof TrackingLinestringClusterDTO);		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void should_returnListWithTwoTrackingCluster_when_processDifferentPlatformPoints() throws Exception {
				
		ProcessClusterMultiElementFunction process = new ProcessClusterMultiElementFunction(objectMapper, zoomLevel);
		
		f1.getProperties().getInTrack().setPlatform(pl1);
		f2.getProperties().getInTrack().setPlatform(pl1);
		f3.getProperties().getInTrack().setPlatform(pl2);
		
		Whitebox.invokeMethod(process, "proccesFeature", f1);
		Whitebox.invokeMethod(process, "proccesFeature", f2);
		Whitebox.invokeMethod(process, "proccesFeature", f3);

		List<BaseTrackingClusterDTO> geometryCollection = (List<BaseTrackingClusterDTO>) process.getResults();
		
		assertEquals(geometryCollection.size(), 2);
		assertTrue(geometryCollection.get(0) instanceof TrackingLinestringClusterDTO);
		assertTrue(geometryCollection.get(1) instanceof TrackingClusterDTO);
	}
	
	private static DateTime getDateTime(String dateString) {
		
		if (dateString == null)
			return null;
		
		Date date = null;
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			throw new ESParseException(e);
		}
		return new DateTime(date);
	}
}
