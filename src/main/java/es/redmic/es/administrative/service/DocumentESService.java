package es.redmic.es.administrative.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.repository.DocumentESRepository;
import es.redmic.es.administrative.taxonomy.repository.SpeciesESRepository;
import es.redmic.es.administrative.taxonomy.service.MisidentificationESService;
import es.redmic.es.administrative.taxonomy.service.SpeciesESService;
import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.geodata.citation.service.CitationESService;
import es.redmic.es.maintenance.device.service.DeviceESService;
import es.redmic.models.es.administrative.dto.DocumentDTO;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;

@Service
public class DocumentESService extends MetaDataESService<Document, DocumentDTO> {

	DocumentESRepository repository;

	@Autowired
	ActivityESService activityESService;

	@Autowired
	ProgramESService programESService;

	@Autowired
	ProjectESService projectESService;

	@Autowired
	SpeciesESRepository speciesESRepository;

	@Autowired
	SpeciesESService speciesESService;

	@Autowired
	DeviceESService deviceESService;

	@Autowired
	CitationESService citationESService;

	@Autowired
	MisidentificationESService misidentificationESService;

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> documentTypeClassInReference = DomainES.class;
	/* Path de elastic para buscar por documentType */
	private String documentTypePropertyPath = "documentType.id";

	@Autowired
	public DocumentESService(DocumentESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	public GeoJSONFeatureCollectionDTO getCitation(DataQueryDTO dto, String documentId) {
		return citationESService.findByDocument(dto, documentId);
	}

	public JSONCollectionDTO getActivities(DataQueryDTO dto, Long id) {

		return activityESService.getActivitiesByDocument(dto, id);
	}

	public JSONCollectionDTO findByIds(DataQueryDTO dto, List<String> documentsIds) {

		DataSearchWrapper<Document> result = repository.findByIds(dto,
				documentsIds.toArray(new String[documentsIds.size()]));

		return orikaMapper.getMapperFacade().map(result.getHits(), JSONCollectionDTO.class, getMappingContext());
	}

	/**
	 * Función para modificar las referencias de documentType en document en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de documentType antes y después
	 *            de ser modificado.
	 */

	public void updateDocumentType(ReferencesES<DomainES> reference) {

		updateReference(reference, documentTypeClassInReference, documentTypePropertyPath);
	}

	@Override
	public void postUpdate(ReferencesES<Document> reference) {

		speciesESService.updateCanaryCatalogue(reference);
		speciesESService.updateEUDirective(reference);
		speciesESService.updateSpainCatalogue(reference);
		// TODO: comprobar cuando se metan documentos
		deviceESService.updateDocument(reference);
		activityESService.updateDocument(reference);
		programESService.updateDocument(reference);
		projectESService.updateDocument(reference);

		misidentificationESService.updateDocument(reference);
	}
}
