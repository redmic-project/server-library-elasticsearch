package es.redmic.es.geodata.infrastructure.mapper;

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
					infrastructureTypeBaseDTOType, null));

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
				classificationListType, null));

		a.setSite(site);
		a.setUpdated(b.getUpdated());
	}
}
