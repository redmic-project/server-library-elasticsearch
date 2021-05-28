package es.redmic.test.unit.geodata.es2dto;

import static org.junit.Assert.assertTrue;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.type.TypeReference;
import org.locationtech.jts.geom.Geometry;

import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.geojson.area.dto.AreaDTO;
import es.redmic.models.es.geojson.citation.dto.CitationDTO;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.models.es.geojson.common.model.GeoSearchWrapper;
import es.redmic.models.es.geojson.geofixedstation.dto.GeoFixedObjectCollectingSeriesDTO;
import es.redmic.models.es.geojson.geofixedstation.dto.GeoFixedTimeSeriesDTO;
import es.redmic.models.es.geojson.infrastructure.dto.InfrastructureDTO;
import es.redmic.models.es.geojson.isolines.dto.IsolinesDTO;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.tracking.animal.dto.AnimalTrackingDTO;
import es.redmic.test.unit.geodata.common.JsonToBeanTestUtil;
import es.redmic.test.utils.ConfigMapper;
import es.redmic.test.utils.GeoFeatureWrapperConfig;
import es.redmic.test.utils.OrikaScanBeanTest;
import ma.glasnost.orika.MappingContext;

public abstract class GeoFeatureDataTestUtil extends JsonToBeanTestUtil {

	protected OrikaScanBeanTest factory = new OrikaScanBeanTest();

	private ConfigMapper configTest;

	public GeoFeatureDataTestUtil(ConfigMapper configTest) throws IOException {
		this.configTest = configTest;
	}

	@Parameters
	public static Collection<ConfigMapper> data() {
		Collection<ConfigMapper> config = new ArrayList<ConfigMapper>();

		// Citation
		config.add(new GeoFeatureWrapperConfig().setDataIn("/geodata/citation/model/searchWrapperCitationModel.json")
				.setDataOut("/geodata/citation/dto/searchWrapperCitationDTO.json").setOutClass(CitationDTO.class)
				.setGeoDataPrefix(DataPrefixType.CITATION));

		config.add(new GeoFeatureWrapperConfig()
				.setDataIn("/geodata/animalTracking/model/searchWrapperAnimalTrackingModel.json")
				.setDataOut("/geodata/animalTracking/dto/searchWrapperAnimalTrackingDTO.json")
				.setOutClass(AnimalTrackingDTO.class).setGeoDataPrefix(DataPrefixType.ANIMAL_TRACKING));

		config.add(new GeoFeatureWrapperConfig()
				.setDataIn("/geodata/timeseries/model/searchWrapperGeoFixedTimeSeriesModel.json")
				.setDataOut("/geodata/timeseries/dto/searchWrapperGeoFixedTimeSeriesDTO.json")
				.setOutClass(GeoFixedTimeSeriesDTO.class).setGeoDataPrefix(DataPrefixType.FIXED_TIMESERIES));

		/*config.add(new GeoFeatureWrapperConfig()
				.setDataIn("/geodata/objectcollecting/model/searchWrapperGeoFixedObjectCollectingModel.json")
				.setDataOut("/geodata/objectcollecting/dto/searchWrapperGeoFixedObjectCollectingDTO.json")
				.setOutClass(GeoFixedObjectCollectingSeriesDTO.class)
				.setGeoDataPrefix(DataPrefixType.OBJECT_COLLECTING));

		config.add(new GeoFeatureWrapperConfig()
				.setDataIn("/geodata/infrastructure/model/searchWrapperInfrastructureModel.json")
				.setDataOut("/geodata/infrastructure/dto/searchWrapperInfrastructureDTO.json")
				.setOutClass(InfrastructureDTO.class).setGeoDataPrefix(DataPrefixType.INFRASTRUCTURE));

		config.add(new GeoFeatureWrapperConfig().setDataIn("/geodata/isolines/model/searchWrapperIsolinesModel.json")
				.setDataOut("/geodata/isolines/dto/searchWrapperIsolinesDTO.json").setOutClass(IsolinesDTO.class)
				.setGeoDataPrefix(DataPrefixType.ISOLINES));

		config.add(new GeoFeatureWrapperConfig().setDataIn("/geodata/area/model/searchWrapperAreaModel.json")
				.setDataOut("/geodata/area/dto/searchWrapperAreaDTO.json").setOutClass(AreaDTO.class)
				.setGeoDataPrefix(DataPrefixType.AREA));*/

		return config;
	}

	//@SuppressWarnings("rawtypes")
	@Test
	public void test() throws IOException, ClassNotFoundException, JSONException {

		TypeReference<GeoSearchWrapper<GeoDataProperties, Geometry>> type = new TypeReference<GeoSearchWrapper<GeoDataProperties, Geometry>>() {
		};

		GeoSearchWrapper beanIn = (GeoSearchWrapper) getBean(configTest.getDataIn(), type);
		String expected = getJsonString(configTest.getDataOut());

		Map<Object, Object> globalProperties = new HashMap<Object, Object>();
		globalProperties.put("targetTypeDto", configTest.getOutClass());
		globalProperties.put("geoDataPrefix", configTest.getGeoDataPrefix());
		MappingContext context = new MappingContext(globalProperties);

		Object beanOut = factory.getMapperFacade().map(beanIn.getHits(), GeoJSONFeatureCollectionDTO.class, context);

		String result = jacksonMapper.writeValueAsString(beanOut);
		JSONAssert.assertEquals(result, expected, true);
		assertTrue(true);
	}
}
