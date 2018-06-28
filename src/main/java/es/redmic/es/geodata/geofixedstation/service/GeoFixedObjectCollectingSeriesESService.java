package es.redmic.es.geodata.geofixedstation.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.geodata.geofixedstation.repository.GeoFixedObjectCollectingSeriesESRepository;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.geojson.common.model.GeoLineStringData;
import es.redmic.models.es.geojson.geofixedstation.dto.GeoFixedObjectCollectingSeriesDTO;
import ma.glasnost.orika.MappingContext;

@Service
public class GeoFixedObjectCollectingSeriesESService extends GeoFixedBaseESService<GeoFixedObjectCollectingSeriesDTO, GeoLineStringData> {

	@Autowired
	public GeoFixedObjectCollectingSeriesESService(GeoFixedObjectCollectingSeriesESRepository respository) {
		super(respository);
	}

	@Override
	public GeoLineStringData mapper(GeoFixedObjectCollectingSeriesDTO dtoToIndex) {
		Map<Object, Object> globalProperties = new HashMap<Object, Object>();
		globalProperties.put("uuid", dtoToIndex.getUuid());
		globalProperties.put("geoDataPrefix", DataPrefixType.OBJECT_COLLECTING);
		MappingContext context = new MappingContext(globalProperties);
		GeoLineStringData model = orikaMapper.getMapperFacade().map(dtoToIndex, GeoLineStringData.class, context);
		if (dtoToIndex.getProperties() != null)
			model.set_parentId(dtoToIndex.getProperties().getActivityId());
		return model;
	}
}
