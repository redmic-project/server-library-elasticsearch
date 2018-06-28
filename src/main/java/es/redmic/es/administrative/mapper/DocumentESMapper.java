package es.redmic.es.administrative.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.maintenance.domain.administrative.service.DocumentTypeESService;
import es.redmic.models.es.administrative.dto.DocumentDTO;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.DocumentTypeDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class DocumentESMapper extends CustomMapper<Document, DocumentDTO> {

	@Autowired
	DocumentTypeESService documentTypeService;

	@Override
	public void mapAtoB(Document a, DocumentDTO b, MappingContext context) {

		if (a.getDocumentType() != null)
			b.setDocumentType(mapperFacade.map(a.getDocumentType(), DocumentTypeDTO.class));
	}

	@Override
	public void mapBtoA(DocumentDTO b, Document a, MappingContext context) {

		if (b.getDocumentType() != null) {
			a.setDocumentType((DomainES) mapperFacade.newObject(b.getDocumentType(), DataMapperUtils.getBaseType(),
					DataMapperUtils.getObjectFactoryContext(documentTypeService)));
		}
	}
}
