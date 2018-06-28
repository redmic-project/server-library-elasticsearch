package es.redmic.es.maintenance.objects.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.HierarchicalESService;
import es.redmic.es.maintenance.objects.repository.ObjectTypeESRepository;
import es.redmic.es.series.objectcollecting.service.ObjectCollectingSeriesESService;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.objects.dto.ObjectClassificationDTO;
import es.redmic.models.es.maintenance.objects.dto.ObjectTypeDTO;
import es.redmic.models.es.maintenance.objects.model.ObjectType;

@Service
public class ObjectTypeESService extends HierarchicalESService<ObjectType, ObjectTypeDTO> {

	@Autowired
	ObjectCollectingSeriesESService objectCollectingSeriesESService;

	@Autowired
	public ObjectTypeESService(ObjectTypeESRepository repository) {
		super(repository);
	}

	public ObjectClassificationDTO getObjectClassification(String objectTypeId) {

		return orikaMapper.getMapperFacade().convert(findById(objectTypeId), ObjectClassificationDTO.class, null);
	}

	@Override
	protected void postUpdate(ReferencesES<ObjectType> reference) {

		super.postUpdate(reference);

		objectCollectingSeriesESService.updateObjectType(reference);
	}
}
