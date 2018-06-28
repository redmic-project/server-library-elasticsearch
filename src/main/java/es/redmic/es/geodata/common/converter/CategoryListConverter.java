package es.redmic.es.geodata.common.converter;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.ElasticSearchUtils;
import es.redmic.models.es.geojson.common.dto.CategoryDTO;
import es.redmic.models.es.geojson.common.dto.CategoryListDTO;
import es.redmic.models.es.geojson.common.model.Aggregations;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

@Component
public class CategoryListConverter extends CustomConverter<Aggregations, CategoryListDTO> {

	@SuppressWarnings("unchecked")
	@Override
	public CategoryListDTO convert(Aggregations source, Type<? extends CategoryListDTO> destinationType) {

		CategoryListDTO categories = new CategoryListDTO();

		Map<String, Object> aggregations = source.getAttributes();
		if (aggregations == null || aggregations.size() == 0)
			return categories;

		for (Map.Entry<String, Object> entry : aggregations.entrySet()) {

			Map<String, Object> value = (Map<String, Object>) entry.getValue();
			List<Map<String, Object>> buckects = ElasticSearchUtils.getBucketsFromAggregations(value);

			if (buckects != null && buckects.size() > 0) {
				CategoryDTO categoryDTO = new CategoryDTO();
				categoryDTO.setField(entry.getKey());

				for (int i = 0; i < buckects.size(); i++) {
					categoryDTO.addId(buckects.get(i).get("key").toString());
				}

				categoryDTO.addDivision(buckects.size());
				categories.add(categoryDTO);
			}
		}
		return categories;
	}
}
