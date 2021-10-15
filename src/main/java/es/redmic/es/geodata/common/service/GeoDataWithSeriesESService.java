package es.redmic.es.geodata.common.service;

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

import org.springframework.beans.factory.annotation.Autowired;

import es.redmic.es.geodata.common.repository.GeoDataESRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.geojson.common.dto.MetaFeatureDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.series.timeseries.model.TimeSeries;

public abstract class GeoDataWithSeriesESService<TDTO extends MetaFeatureDTO<?, ?>, TModel extends Feature<GeoDataProperties, ?>>
		extends GeoDataESService<TDTO, TModel> {

	//@Autowired
	//TimeSeriesESService timeSeriesESService;

	public GeoDataWithSeriesESService() {
	}

	public GeoDataWithSeriesESService(GeoDataESRepository<TModel> repository) {
		super(repository);
	}

	@Override
	public TModel save(TModel modelToIndex) {

		TModel result = super.save(modelToIndex);
		//timeSeriesESService.save(getSeries(modelToIndex));
		return result;
	}

	@Override
	public TModel update(TModel modelToIndex) {

		TModel result = super.update(modelToIndex);
		//timeSeriesESService.update(getSeries(modelToIndex));
		return result;
	}

	/*
	 * Obtiene el modelo de series a indexar en timeseries. Para este tipo solo
	 * debe tener uno
	 */
	/*-private TimeSeries getSeries(TModel modelToIndex) {

		List<TimeSeries> series = modelToIndex.getProperties().getSeries();
		// TODO: crear excepción propia
		if (series == null || series.size() != 1) {
			LOGGER.debug("No se ha mapeado correctamente los datos de isolines en la serie");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
		return series.get(0);
	}-*/
}
