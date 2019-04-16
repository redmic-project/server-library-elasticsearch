package es.redmic.es.series.timeseries.service;

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

import java.util.UUID;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.es.common.service.ISettingsService;
import es.redmic.es.common.service.SettingsServices;
import es.redmic.es.series.timeseries.repository.TimeSeriesSettingsESRepository;
import es.redmic.models.es.series.timeseries.dto.TimeSeriesSettingsDTO;
import es.redmic.models.es.series.timeseries.model.TimeSeriesSettings;

//@Service
public class TimeSeriesSettingsESService extends SettingsServices<TimeSeriesSettings, TimeSeriesSettingsDTO>
		implements ISettingsService<TimeSeriesSettings, TimeSeriesSettingsDTO> {

	@Autowired
	TimeSeriesSettingsESRepository repository;

	@Autowired
	protected ObjectMapper objectMapper;

	public TimeSeriesSettingsESService() {

	}

	@Override
	public TimeSeriesSettings save(TimeSeriesSettingsDTO dto) {

		String userId = userService.getUserId();

		TimeSeriesSettings model = objectMapper.convertValue(dto, TimeSeriesSettings.class);

		model.setUserId(userId);
		model.setDate(new DateTime().toDateTimeISO());

		if (model.getId() != null) {

			return repository.update(model);
		} else {
			model.setId(UUID.randomUUID().toString());
			return repository.save(model);
		}
	}

	@Override
	public TimeSeriesSettings update(TimeSeriesSettingsDTO dto) {

		String userId = userService.getUserId();
		TimeSeriesSettings model = objectMapper.convertValue(dto, TimeSeriesSettings.class);

		model.setUserId(userId);
		model.setDate(new DateTime().toDateTimeISO());

		return repository.update(model);
	}
}
