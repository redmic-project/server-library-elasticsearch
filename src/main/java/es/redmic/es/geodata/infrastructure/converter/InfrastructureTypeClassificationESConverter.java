package es.redmic.es.geodata.infrastructure.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.maintenance.point.service.InfrastructureTypeESService;
import es.redmic.models.es.maintenance.common.model.ClassificationBase;
import es.redmic.models.es.maintenance.common.model.ClassificationItem;
import es.redmic.models.es.maintenance.point.dto.InfrastructureTypeBaseDTO;
import es.redmic.models.es.maintenance.point.model.InfrastructureType;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

@Component
public class InfrastructureTypeClassificationESConverter
		extends BidirectionalConverter<InfrastructureTypeBaseDTO, List<ClassificationItem>> {

	@Autowired
	InfrastructureTypeESService infrastructureTypeESService;

	@Override
	public List<ClassificationItem> convertTo(InfrastructureTypeBaseDTO source,
			Type<List<ClassificationItem>> destinationType) {

		List<ClassificationItem> result = new ArrayList<ClassificationItem>();

		ClassificationItem type = getClassificationItem(source.getId().toString());
		// Guardamos el más específico
		result.add(type);
		// Guardamos los ancestors
		String[] pathSplitted = type.getType().getPath().split("\\."),
				ancestorsIds = Arrays.copyOfRange(pathSplitted, 1, pathSplitted.length - 1);

		for (int i = 0; i < ancestorsIds.length; i++) {
			result.add(getClassificationItem(ancestorsIds[i]));
		}
		return result;
	}

	@Override
	public InfrastructureTypeBaseDTO convertFrom(List<ClassificationItem> source,
			Type<InfrastructureTypeBaseDTO> destinationType) {

		if (source == null || source.size() == 0)
			return null;

		ClassificationBase specificItem = source.get(0).getType();

		for (int i = 1; i < source.size(); i++) {
			if (source.get(i).getType().getLevel() > specificItem.getLevel())
				specificItem = source.get(i).getType();
		}
		return mapperFacade.map(specificItem, InfrastructureTypeBaseDTO.class);
	}

	private ClassificationItem getClassificationItem(String id) {

		ClassificationItem classificationItem = new ClassificationItem();
		InfrastructureType item = infrastructureTypeESService.findById(id);
		classificationItem.setType(mapperFacade.map(item, ClassificationBase.class));
		return classificationItem;
	}
}