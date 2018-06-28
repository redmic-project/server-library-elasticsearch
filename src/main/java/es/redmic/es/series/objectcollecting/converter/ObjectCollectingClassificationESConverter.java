package es.redmic.es.series.objectcollecting.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.maintenance.objects.repository.ObjectTypeESRepository;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.maintenance.objects.dto.ObjectClassificationDTO;
import es.redmic.models.es.maintenance.objects.dto.ObjectCollectingDTO;
import es.redmic.models.es.maintenance.objects.dto.ObjectTypeBaseDTO;
import es.redmic.models.es.maintenance.objects.model.ObjectType;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

@Component
public class ObjectCollectingClassificationESConverter
		extends BidirectionalConverter<ObjectType, ObjectClassificationDTO> {

	@Autowired
	ObjectTypeESRepository repository;

	@SuppressWarnings("unchecked")
	@Override
	public ObjectClassificationDTO convertTo(ObjectType source, Type<ObjectClassificationDTO> destinationType) {

		ObjectClassificationDTO result = new ObjectClassificationDTO();// mapperFacade.map(source.getObjectGroup(),
																		// ObjectClassificationDTO.class);

		List<ObjectCollectingDTO> classification = new ArrayList<ObjectCollectingDTO>();

		// Guardamos el más específico
		classification.add(getObjectDTO(source));
		// Guardamos los ancestors
		String[] pathSplitted = source.getPath().split("\\."),
				ancestorsIds = Arrays.copyOfRange(pathSplitted, 1, pathSplitted.length - 1);

		for (int i = 0; i < ancestorsIds.length; i++) {
			DataHitWrapper<ObjectType> item = (DataHitWrapper<ObjectType>) repository.findById(ancestorsIds[i]);
			classification.add(getObjectDTO(item.get_source()));
		}
		result.setClassification(classification);

		return result;
	}

	@Override
	public ObjectType convertFrom(ObjectClassificationDTO source, Type<ObjectType> destinationType) {
		return null;
	}

	private ObjectCollectingDTO getObjectDTO(ObjectType obj) {

		ObjectCollectingDTO objectCollectingDTO = new ObjectCollectingDTO();
		ObjectTypeBaseDTO item = mapperFacade.map(obj, ObjectTypeBaseDTO.class);
		objectCollectingDTO.setObjectType(item);

		return objectCollectingDTO;
	}
}