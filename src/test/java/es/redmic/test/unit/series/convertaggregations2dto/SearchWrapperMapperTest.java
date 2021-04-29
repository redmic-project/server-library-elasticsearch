package es.redmic.test.unit.series.convertaggregations2dto;

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

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import es.redmic.es.series.common.converter.DataHistogramConverter;
import es.redmic.es.series.common.converter.ItemHistogramConverter;
import es.redmic.es.series.common.converter.ItemStatsHistogramConverter;
import es.redmic.es.series.objectcollecting.converter.ClassificationForListConverter;
import es.redmic.es.series.objectcollecting.converter.ClassificationForPieChartConverter;
import es.redmic.test.utils.ConfigMapper;

@RunWith(Parameterized.class)
public class SearchWrapperMapperTest extends SeriesDataTestUtil {

	public SearchWrapperMapperTest(ConfigMapper configTest) throws IOException {
		super(configTest);

		// @formatter:off
		factory.addConverter(new ClassificationForListConverter());

		factory.addConverter(new ClassificationForPieChartConverter());

		factory.addConverter(new DataHistogramConverter());

		factory.addConverter(new ItemHistogramConverter());
		factory.addConverter(new ItemStatsHistogramConverter());
		// @formatter:on
	}
}
