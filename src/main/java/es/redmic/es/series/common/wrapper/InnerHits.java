package es.redmic.es.series.common.wrapper;

/*-
 * #%L
 * ElasticSearch
 * %%
 * Copyright (C) 2019 - 2021 REDMIC Project / Server
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import es.redmic.models.es.series.attributeseries.model.AttributeSeries;
import es.redmic.models.es.series.objectcollecting.model.ObjectCollectingSeries;
import es.redmic.models.es.series.timeseries.model.TimeSeries;

@JsonIgnoreProperties(ignoreUnknown=true)
public class InnerHits {

	SeriesSearchWrapper<TimeSeries> timeseries;
	SeriesSearchWrapper<ObjectCollectingSeries> collecting;
	SeriesSearchWrapper<AttributeSeries> attributeseries;

	public SeriesSearchWrapper<TimeSeries> getTimeseries() {
		return timeseries;
	}

	public void setTimeseries(SeriesSearchWrapper<TimeSeries> timeseries) {
		this.timeseries = timeseries;
	}

	public SeriesSearchWrapper<ObjectCollectingSeries> getCollecting() {
		return collecting;
	}

	public void setCollecting(SeriesSearchWrapper<ObjectCollectingSeries> collecting) {
		this.collecting = collecting;
	}

	public SeriesSearchWrapper<AttributeSeries> getAttributeseries() {
		return attributeseries;
	}

	public void setAttributeseries(SeriesSearchWrapper<AttributeSeries> attributeseries) {
		this.attributeseries = attributeseries;
	}
}
