package es.redmic.es.geodata.infrastructure.mapper;

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

import org.springframework.stereotype.Component;

import es.redmic.models.es.geojson.common.model.InnerHits;
import es.redmic.models.es.geojson.infrastructure.dto.InfrastructurePropertiesDTO;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.properties.model.Site;
import es.redmic.models.es.maintenance.common.model.ClassificationItem;
import es.redmic.models.es.maintenance.point.dto.InfrastructureTypeBaseDTO;
import es.redmic.models.es.series.attributeseries.dto.AttributeSeriesDTO;
import es.redmic.models.es.series.attributeseries.model.AttributeSeries;
import es.redmic.models.es.series.common.model.SeriesHitWrapper;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;

@Component
public class InfrastructurePropertiesESMapper extends CustomMapper<GeoDataProperties, InfrastructurePropertiesDTO> {

	final Type<InfrastructureTypeBaseDTO> infrastructureTypeBaseDTOType = new TypeBuilder<InfrastructureTypeBaseDTO>() {
	}.build();
	final Type<List<ClassificationItem>> classificationListType = new TypeBuilder<List<ClassificationItem>>() {
	}.build();

	@Override
	public void mapAtoB(GeoDataProperties a, InfrastructurePropertiesDTO b, MappingContext context) {

		mapperFacade.map(a.getSite(), b);
		b.setUpdated(a.getUpdated());

		if (a.getSite().getClassification() != null)
			b.setInfrastructureType(mapperFacade.convert(a.getSite().getClassification(), classificationListType,
					infrastructureTypeBaseDTOType, null, null));

		InnerHits inner_hits = (InnerHits) context.getProperty("inner_hits");

		if (inner_hits != null && inner_hits.getAttributeseries() != null) {

			List<SeriesHitWrapper<AttributeSeries>> attributeSeries = inner_hits.getAttributeseries().getHits()
					.getHits();

			List<AttributeSeriesDTO> attributes = new ArrayList<AttributeSeriesDTO>();

			for (SeriesHitWrapper<AttributeSeries> attributeSeriesItem : attributeSeries) {

				attributes.add(mapperFacade.map(attributeSeriesItem, AttributeSeriesDTO.class));
			}

			b.setAttributes(attributes);
		}
	}

	@Override
	public void mapBtoA(InfrastructurePropertiesDTO b, GeoDataProperties a, MappingContext context) {

		Site site = mapperFacade.map(b, Site.class);

		site.setClassification(mapperFacade.convert(b.getInfrastructureType(), infrastructureTypeBaseDTOType,
				classificationListType, null, null));

		a.setSite(site);
		a.setUpdated(b.getUpdated());
	}
}
