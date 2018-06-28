package es.redmic.es.administrative.taxonomy.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.service.ActivityESService;
import es.redmic.es.administrative.service.DocumentESService;
import es.redmic.es.administrative.taxonomy.repository.SpeciesESRepository;
import es.redmic.models.es.administrative.dto.ActivityDTO;
import es.redmic.models.es.administrative.dto.ActivityDocumentDTO;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.administrative.model.DocumentCompact;
import es.redmic.models.es.administrative.taxonomy.dto.SpeciesDTO;
import es.redmic.models.es.administrative.taxonomy.model.Species;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

@Service
public class SpeciesESService extends TaxonBaseESService<Species, SpeciesDTO> {

	private String rankId = "10";

	@Autowired
	ActivityESService activityESService;

	@Autowired
	DocumentESService documentESService;

	private static Boolean isNestedProperty = false;

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> trophicRegimeClassInReference = DomainES.class;
	/* Path de elastic para buscar por canaryProtection */
	private String trophicRegimePropertyPath = "peculiarity.trophicRegime.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> canaryProtectionClassInReference = DomainES.class;
	/* Path de elastic para buscar por canaryProtection */
	private String canaryProtectionPropertyPath = "peculiarity.canaryProtection.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> ecologyClassInReference = DomainES.class;
	/* Path de elastic para buscar por ecology */
	private String ecologyPropertyPath = "peculiarity.ecology.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> euProtectionClassInReference = DomainES.class;
	/* Path de elastic para buscar por euProtection */
	private String euProtectionPropertyPath = "peculiarity.euProtection.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> interestClassInReference = DomainES.class;
	/* Path de elastic para buscar por interest */
	private String interestPropertyPath = "peculiarity.interest.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> endemicityClassInReference = DomainES.class;
	/* Path de elastic para buscar por endemicity */
	private String endemicityPropertyPath = "peculiarity.endemicity.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> originClassInReference = DomainES.class;
	/* Path de elastic para buscar por origin */
	private String originPropertyPath = "peculiarity.origin.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> permanenceClassInReference = DomainES.class;
	/* Path de elastic para buscar por permanence */
	private String permanencePropertyPath = "peculiarity.permanence.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> spainProtectionClassInReference = DomainES.class;
	/* Path de elastic para buscar por spainProtection */
	private String spainProtectionPropertyPath = "peculiarity.spainProtection.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DocumentCompact> spainCatalogueClassInReference = DocumentCompact.class;
	/* Path de elastic para buscar por spainCatalogue */
	private String spainCataloguePropertyPath = "peculiarity.spainCatalogue.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DocumentCompact> canaryCatalogueClassInReference = DocumentCompact.class;
	/* Path de elastic para buscar por canaryCatalogue */
	private String canaryCataloguePropertyPath = "peculiarity.canaryCatalogue.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DocumentCompact> euDirectiveClassInReference = DocumentCompact.class;
	/* Path de elastic para buscar por canaryCatalogue */
	private String euDirectivePropertyPath = "peculiarity.euDirective.id";

	@Autowired
	public SpeciesESService(SpeciesESRepository repository) {
		super(repository);
	}

	public JSONCollectionDTO getActivities(DataQueryDTO dto, String speciesId) {

		return activityESService.findBySpecies(dto, speciesId);
	}

	@SuppressWarnings("unchecked")
	public JSONCollectionDTO getDocuments(DataQueryDTO dto, String speciesId) {

		JSONCollectionDTO activityHits = activityESService.findBySpecies(dto, speciesId);

		List<ActivityDTO> activities = activityHits.getData();

		List<String> fields = new ArrayList<String>();
		fields.add("documents");

		List<String> documentsIds = new ArrayList<String>();
		for (int i = 0; i < activities.size(); i++) {
			List<ActivityDocumentDTO> documents = activities.get(i).getDocuments();
			if (documents != null)
				for (int j = 0; j < documents.size(); j++)
					documentsIds.add(Long.toString(documents.get(j).getDocument().getId()));
		}
		return documentESService.findByIds(dto, documentsIds);
	}

	/**
	 * Función para modificar las referencias de canaryProtection en species en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de canaryProtection antes y
	 *            después de ser modificado.
	 */

	public void updateCanaryProtection(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, canaryProtectionClassInReference, canaryProtectionPropertyPath,
				isNestedProperty);
	}

	/**
	 * Función para modificar las referencias de trophicregime en species en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de trophicregime antes y después
	 *            de ser modificado.
	 */

	public void updateTrophicRegime(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, trophicRegimeClassInReference, trophicRegimePropertyPath, isNestedProperty);
	}

	/**
	 * Función para modificar las referencias de ecology en species en caso de
	 * ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de ecology antes y después de
	 *            ser modificado.
	 */

	public void updateEcology(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, ecologyClassInReference, ecologyPropertyPath, isNestedProperty);
	}

	/**
	 * Función para modificar las referencias de endemicity en species en caso
	 * de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de endemicity antes y después de
	 *            ser modificado.
	 */

	public void updateEndemicity(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, endemicityClassInReference, endemicityPropertyPath, isNestedProperty);
	}

	/**
	 * Función para modificar las referencias de euProtection en species en caso
	 * de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de euProtection antes y después
	 *            de ser modificado.
	 */

	public void updateEUProtection(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, euProtectionClassInReference, euProtectionPropertyPath, isNestedProperty);
	}

	/**
	 * Función para modificar las referencias de interest en species en caso de
	 * ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de interest antes y después de
	 *            ser modificado.
	 */

	public void updateInterest(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, interestClassInReference, interestPropertyPath, isNestedProperty);
	}

	/**
	 * Función para modificar las referencias de origin en species en caso de
	 * ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de origin antes y después de ser
	 *            modificado.
	 */

	public void updateOrigin(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, originClassInReference, originPropertyPath, isNestedProperty);
	}

	/**
	 * Función para modificar las referencias de permanence en species en caso
	 * de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de permanence antes y después de
	 *            ser modificado.
	 */

	public void updatePermanence(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, permanenceClassInReference, permanencePropertyPath, isNestedProperty);
	}

	/**
	 * Función para modificar las referencias de spainProtection en species en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de spainProtection antes y
	 *            después de ser modificado.
	 */

	public void updateSpainProtection(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, spainProtectionClassInReference, spainProtectionPropertyPath,
				isNestedProperty);
	}

	/**
	 * Función para modificar las referencias de spainCatalogue en species en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de spainCatalogue antes y
	 *            después de ser modificado.
	 */

	public void updateSpainCatalogue(ReferencesES<Document> reference) {

		updateReferenceByScript(reference, spainCatalogueClassInReference, spainCataloguePropertyPath,
				isNestedProperty);
	}

	/**
	 * Función para modificar las referencias de canaryCatalogue en species en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de canaryCatalogue antes y
	 *            después de ser modificado.
	 */

	public void updateCanaryCatalogue(ReferencesES<Document> reference) {

		updateReferenceByScript(reference, canaryCatalogueClassInReference, canaryCataloguePropertyPath,
				isNestedProperty);
	}

	/**
	 * Función para modificar las referencias de euDirective en species en caso
	 * de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de euDirective antes y después
	 *            de ser modificado.
	 */

	public void updateEUDirective(ReferencesES<Document> reference) {

		updateReferenceByScript(reference, euDirectiveClassInReference, euDirectivePropertyPath, isNestedProperty);
	}

	@Override
	public void postUpdate(ReferencesES<Species> reference) {

		super.postUpdate(reference);

		citationESService.updateTaxon(reference);
		misidentificationESService.updateTaxon(reference);
		animalESService.updateTaxon(reference);
	}

	@Override
	public String getRankId() {
		return rankId;
	}

	@Override
	public void setRankId(String rankId) {
		this.rankId = rankId;
	}
}
