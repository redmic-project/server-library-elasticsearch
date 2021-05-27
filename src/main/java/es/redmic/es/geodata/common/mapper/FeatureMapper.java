package es.redmic.es.geodata.common.mapper;

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

import org.springframework.stereotype.Component;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;

import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.geojson.common.dto.MetaFeatureDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.common.model.GeoHitWrapper;
import es.redmic.utils.geo.performance.Simplify;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class FeatureMapper extends CustomMapper<GeoHitWrapper, MetaFeatureDTO> {

	@SuppressWarnings("unchecked")
	@Override
	public void mapAtoB(GeoHitWrapper a, MetaFeatureDTO b, MappingContext context) {

		Class<?> targetTypeDto = (Class<?>) context.getProperty("targetTypeDto");

		if (targetTypeDto == null)
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);

		context.setProperty("inner_hits", a.getInner_hits());

		Feature source = a.get_source();

		MetaFeatureDTO featureDTO = (MetaFeatureDTO) mapperFacade.map(source, targetTypeDto, context);
		b.setProperties(featureDTO.getProperties());

		Geometry geom = a.get_source().getGeometry();

		if (geom != null) {

			// TODO Leer el zoom desde el contexto
			if (!Point.class.isInstance(geom) && !MultiPolygon.class.isInstance(geom))
				b.setGeometry(Simplify.simplify(geom, 9));
			else
				b.setGeometry(geom);
		}

		b.setId(featureDTO.getId());
		b.setUuid(featureDTO.getUuid());

		if (featureDTO.getProperties() != null) {
			b.getProperties().setActivityId(a.get_source().getProperties().getActivityId());
		}

		b.get_meta().setScore(a.get_score());
		b.get_meta().setVersion(a.get_version());
		b.get_meta().setHighlight(a.getHighlight());
	}
}
