package es.redmic.es.maintenance.domain.administrative.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.service.DocumentESService;
import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.maintenance.domain.administrative.repository.DocumentTypeESRepository;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.models.es.maintenance.administrative.dto.DocumentTypeDTO;

@Service
public class DocumentTypeESService extends MetaDataESService<DomainES, DocumentTypeDTO> {

	@Autowired
	DocumentESService documentESService;

	DocumentTypeESRepository repository;

	@Autowired
	public DocumentTypeESService(DocumentTypeESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	@Override
	public void postUpdate(ReferencesES<DomainES> reference) {
		documentESService.updateDocumentType(reference);
	}

	public DocumentTypeDTO findByName(String name) {
		DataSearchWrapper<DomainES> result = repository.findByName(name);
		JSONCollectionDTO jsonCollect = orikaMapper.getMapperFacade().map(result.getHits(), JSONCollectionDTO.class,
				getMappingContext());

		if (jsonCollect == null || jsonCollect.getData() == null || jsonCollect.getData().size() != 1)
			throw new ItemNotFoundException("name", name);

		return (DocumentTypeDTO) jsonCollect.getData().get(0);
	}
}
