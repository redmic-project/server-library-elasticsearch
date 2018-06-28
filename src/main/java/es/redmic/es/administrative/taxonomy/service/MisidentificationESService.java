package es.redmic.es.administrative.taxonomy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.taxonomy.repository.MisidentificationESRepository;
import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.geodata.citation.service.CitationESService;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.administrative.model.DocumentCompact;
import es.redmic.models.es.administrative.taxonomy.dto.MisidentificationDTO;
import es.redmic.models.es.administrative.taxonomy.model.Misidentification;
import es.redmic.models.es.administrative.taxonomy.model.Species;
import es.redmic.models.es.administrative.taxonomy.model.TaxonAncestorsCompact;
import es.redmic.models.es.common.model.ReferencesES;

@Service
public class MisidentificationESService extends MetaDataESService<Misidentification, MisidentificationDTO> {

	@Autowired
	private CitationESService citationService;

	/* Path de elastic para buscar por taxon */
	private String taxonPropertyPath = "taxon.id";
	/* Path de elastic para buscar por taxon */
	private String badIdentityPropertyPath = "badIdentity.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<TaxonAncestorsCompact> taxonClassInReference = TaxonAncestorsCompact.class;

	/* Clase del modelo indexado en la referencia */
	private static Class<DocumentCompact> documentClassInReference = DocumentCompact.class;
	/* Path de elastic para buscar por document */
	private String documentPropertyPath = "document.id";

	@Autowired
	public MisidentificationESService(MisidentificationESRepository repository) {
		super(repository);
	}

	/**
	 * Función para modificar las referencias de taxon en misidentification en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de taxon antes y después de ser
	 *            modificado.
	 */

	public void updateTaxon(ReferencesES<Species> reference) {

		updateReference(reference, taxonClassInReference, taxonPropertyPath);
		updateReference(reference, taxonClassInReference, badIdentityPropertyPath);
	}

	/**
	 * Función para modificar las referencias de document en misidentification
	 * en caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de document antes y después de
	 *            ser modificado.
	 */

	public void updateDocument(ReferencesES<Document> reference) {

		updateReference(reference, documentClassInReference, documentPropertyPath);
	}

	@Override
	public void postUpdate(ReferencesES<Misidentification> reference) {

		citationService.updateMisidentification(reference);
	}
}