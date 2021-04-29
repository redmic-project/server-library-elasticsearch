package es.redmic.test.unit.geodata.es2dto;

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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fasterxml.jackson.core.type.TypeReference;
import org.locationtech.jts.geom.Geometry;

import es.redmic.es.geodata.area.mapper.AreaESMapper;
import es.redmic.es.geodata.area.mapper.AreaPropertiesESMapper;
import es.redmic.es.geodata.area.repository.AreaESRepository;
import es.redmic.es.geodata.citation.mapper.CitationESMapper;
import es.redmic.es.geodata.citation.mapper.CitationPropertiesESMapper;
import es.redmic.es.geodata.common.converter.CustomIdConverter;
import es.redmic.es.geodata.common.mapper.FeatureCollectionMapper;
import es.redmic.es.geodata.common.mapper.FeatureMapper;
import es.redmic.es.geodata.geofixedstation.mapper.DataDefinitionESMapper;
import es.redmic.es.geodata.geofixedstation.mapper.FixedSurveySeriesPropertiesESMapper;
import es.redmic.es.geodata.geofixedstation.mapper.MeasurementESMapper;
import es.redmic.es.geodata.geofixedstation.mapper.SiteESMapper;
import es.redmic.es.geodata.infrastructure.converter.InfrastructureTypeClassificationESConverter;
import es.redmic.es.geodata.infrastructure.mapper.InfrastructureESMapper;
import es.redmic.es.geodata.infrastructure.mapper.InfrastructurePropertiesESMapper;
import es.redmic.es.geodata.isolines.converter.LineTypeClassificationESConverter;
import es.redmic.es.geodata.isolines.mapper.IsolinesESMapper;
import es.redmic.es.geodata.isolines.mapper.IsolinesPropertiesESMapper;
import es.redmic.es.geodata.tracking.animal.mapper.AnimalTrackingESMapper;
import es.redmic.es.geodata.tracking.animal.mapper.AnimalTrackingPropertiesESMapper;
import es.redmic.es.maintenance.area.mapper.AreaClassificationESMapper;
import es.redmic.es.series.common.mapper.SeriesItemMapper;
import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.test.utils.ConfigMapper;

@RunWith(Parameterized.class)
public class SearchWrapperMapperTest extends GeoFeatureDataTestUtil {

	@Mock
	AreaESRepository areaESRepository;

	@InjectMocks
	AreaPropertiesESMapper areaPropertiesESMapper;

	String areaPath = "/geodata/area/model/areaParent.json";

	public SearchWrapperMapperTest(ConfigMapper configTest) throws IOException {
		super(configTest);

		// @formatter:off
		factory.addConverter(new CustomIdConverter());
		factory.addMapper(new FeatureCollectionMapper());
		factory.addMapper(new FeatureMapper());
		factory.addMapper(new AnimalTrackingPropertiesESMapper());
		factory.addMapper(new AnimalTrackingESMapper());
		factory.addMapper(new CitationPropertiesESMapper());
		factory.addMapper(new CitationESMapper());
		factory.addMapper(new SiteESMapper());
		factory.addMapper(new DataDefinitionESMapper());
		factory.addMapper(new MeasurementESMapper());
		factory.addMapper(new FixedSurveySeriesPropertiesESMapper());

		factory.addConverter(new InfrastructureTypeClassificationESConverter());
		factory.addMapper(new InfrastructurePropertiesESMapper());
		factory.addMapper(new InfrastructureESMapper());

		factory.addMapper(new SeriesItemMapper());

		factory.addConverter(new LineTypeClassificationESConverter());
		factory.addMapper(new IsolinesPropertiesESMapper());
		factory.addMapper(new IsolinesESMapper());

		factory.addMapper(new AreaClassificationESMapper());
		factory.addMapper(new AreaESMapper());
		// @formatter:on
	}

	/*
	 * Añade mapper especiales a los cuales hay que inyectar dependencias
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setupTest() throws IOException {

		initMocks(this);

		factory.addMapper(areaPropertiesESMapper);

		TypeReference<GeoHitWrapper<GeoDataProperties, Geometry>> type = new TypeReference<GeoHitWrapper<GeoDataProperties, Geometry>>() {
		};

		GeoHitWrapper parent = (GeoHitWrapper) getBean(areaPath, type);

		when(areaESRepository.findBySamplingId(anyString())).thenReturn(parent);
	}
}
