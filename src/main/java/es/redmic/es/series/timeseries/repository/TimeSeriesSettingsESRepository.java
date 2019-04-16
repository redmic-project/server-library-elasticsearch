package es.redmic.es.series.timeseries.repository;

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

import es.redmic.es.common.repository.SettingsRepository;
import es.redmic.models.es.series.timeseries.dto.TimeSeriesSettingsDTO;
import es.redmic.models.es.series.timeseries.model.TimeSeriesSettings;


//@Repository
public class TimeSeriesSettingsESRepository extends SettingsRepository<TimeSeriesSettings, TimeSeriesSettingsDTO> {

	private static String[] INDEX = { "user" };
	private static String[] TYPE = { "chartTimeSeries" };

	public TimeSeriesSettingsESRepository() {
		super(INDEX, TYPE);
	}
}
