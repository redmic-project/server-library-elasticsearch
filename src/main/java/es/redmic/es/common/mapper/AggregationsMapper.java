package es.redmic.es.common.mapper;

import org.springframework.stereotype.Component;

import es.redmic.models.es.common.dto.AggregationsDTO;
import es.redmic.models.es.geojson.common.model.Aggregations;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class AggregationsMapper extends CustomMapper<Aggregations, AggregationsDTO> {

	@Override
	public void mapAtoB(Aggregations a, AggregationsDTO b, MappingContext context) {
		b.setAttributes(a.getAttributes());
	}
}
