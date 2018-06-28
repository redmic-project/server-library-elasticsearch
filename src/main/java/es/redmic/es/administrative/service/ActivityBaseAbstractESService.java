package es.redmic.es.administrative.service;

import org.springframework.beans.factory.annotation.Autowired;

import es.redmic.es.administrative.repository.ActivityBaseESRepository;
import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.es.maintenance.domain.administrative.service.ActivityRankESService;
import es.redmic.models.es.administrative.dto.ActivityBaseDTO;
import es.redmic.models.es.administrative.model.ActivityBase;
import es.redmic.models.es.administrative.model.Contact;
import es.redmic.models.es.administrative.model.ContactCompact;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.administrative.model.DocumentCompact;
import es.redmic.models.es.administrative.model.Organisation;
import es.redmic.models.es.administrative.model.OrganisationCompact;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.administrative.model.PlatformCompact;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;

public abstract class ActivityBaseAbstractESService<TModel extends ActivityBase, TDTO extends ActivityBaseDTO>
		extends MetaDataESService<TModel, TDTO> {

	private String rankId = "3";

	@Autowired
	private ActivityRankESService activityRankService;

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> accessibilityClassInReference = DomainES.class;
	/* Path de elastic para buscar por accessibility */
	private String accessibilityPropertyPath = "accessibility.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> scopeClassInReference = DomainES.class;
	/* Path de elastic para buscar por scope */
	private String scopePropertyPath = "scope.id";

	/* Path de elastic para buscar por platform */
	private String platformPropertyPath = "platforms.platform.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<PlatformCompact> platformClassInReference = PlatformCompact.class;

	/* Path de elastic para buscar por organisation */
	private String organisationPropertyPath = "organisations.organisation.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<OrganisationCompact> organisationClassInReference = OrganisationCompact.class;

	/*
	 * Path de elastic para buscar por organisation en contactos de actividades
	 */
	private String organisationsContactPropertyPath = "contacts.organisation.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<OrganisationCompact> organisationsContactClassInReference = OrganisationCompact.class;

	/* Path de elastic para buscar por contact */
	private String contactPropertyPath = "contacts.contact.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<ContactCompact> contactClassInReference = ContactCompact.class;

	/*
	 * Path de elastic para buscar por contactos de plataformas en actividades
	 */
	private String contactsPlatformPropertyPath = "platforms.contact.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<ContactCompact> contactsPlatformClassInReference = ContactCompact.class;

	/* Path de elastic para buscar por document */
	private String documentPropertyPath = "documents.document.id";
	/* Clase del modelo indexado en la referencia */
	private static Class<DocumentCompact> documentClassInReference = DocumentCompact.class;

	/* Path de elastic para buscar por rol de organización */
	private String organisationRolesPropertyPath = "organisations.role.id";
	/* Path de elastic para buscar por platform */
	private String contactOrganisationRolesPropertyPath = "contacts.role.id";
	/* Path de elastic para buscar por rol de un contacto de una plataforma */
	private String platformContactRolesPropertyPath = "platforms.role.id";

	int nestingDepthRelations = 2;

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> roleClassInReference = DomainES.class;

	@SuppressWarnings("unchecked")
	@Autowired
	public ActivityBaseAbstractESService(ActivityBaseESRepository repository) {
		super((RWDataESRepository<TModel>) repository);
	}

	public ActivityBaseAbstractESService(RWDataESRepository<TModel> repository) {
		super(repository);
	}

	public DomainES getRank(String rankId) {
		return activityRankService.findById(rankId);
	}

	/**
	 * Función para modificar las referencias de accessibility en activity en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de accessibility antes y después
	 *            de ser modificado.
	 */

	public void updateAccessibility(ReferencesES<DomainES> reference) {

		updateReference(reference, accessibilityClassInReference, accessibilityPropertyPath);
	}

	/**
	 * Función para modificar las referencias de scope en activity en caso de
	 * ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de scope antes y después de ser
	 *            modificado.
	 */

	public void updateScope(ReferencesES<DomainES> reference) {

		updateReference(reference, scopeClassInReference, scopePropertyPath);
	}

	/**
	 * Función para modificar las referencias de platform en activity en caso de
	 * ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de platform antes y después de
	 *            ser modificado.
	 */

	public void updatePlatform(ReferencesES<Platform> reference) {

		updateReferenceByScript(reference, platformClassInReference, platformPropertyPath, nestingDepthRelations);
	}

	/**
	 * Función para modificar las referencias de organisation en activity en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de organisation antes y después
	 *            de ser modificado.
	 */

	public void updateOrganisation(ReferencesES<Organisation> reference) {

		updateReferenceByScript(reference, organisationClassInReference, organisationPropertyPath,
				nestingDepthRelations);
	}

	/**
	 * Función para modificar las referencias de organisation en contactos de
	 * activity en caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de organisation antes y después
	 *            de ser modificado.
	 */

	public void updateOrganisationsContact(ReferencesES<Organisation> reference) {

		updateReferenceByScript(reference, organisationsContactClassInReference, organisationsContactPropertyPath,
				nestingDepthRelations);
	}

	/**
	 * Función para modificar las referencias de contacts en activity en caso de
	 * ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de contact antes y después de
	 *            ser modificado.
	 */

	public void updateContact(ReferencesES<Contact> reference) {

		updateReferenceByScript(reference, contactClassInReference, contactPropertyPath, nestingDepthRelations);
	}

	/**
	 * Función para modificar las referencias de contact en plataform de
	 * activity en caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de contact antes y después de
	 *            ser modificado.
	 */

	public void updateContactsPlatform(ReferencesES<Contact> reference) {

		updateReferenceByScript(reference, contactsPlatformClassInReference, contactsPlatformPropertyPath,
				nestingDepthRelations);
	}

	/**
	 * Función para modificar las referencias de document en activity en caso de
	 * ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de document antes y después de
	 *            ser modificado.
	 */

	public void updateDocument(ReferencesES<Document> reference) {

		updateReferenceByScript(reference, documentClassInReference, documentPropertyPath, nestingDepthRelations);
	}

	/**
	 * Función para modificar las referencias de roles en organisation dentro de
	 * activity en caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de organisationRoles antes y
	 *            después de ser modificado.
	 */

	public void updateOrganisationRoles(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, roleClassInReference, organisationRolesPropertyPath, nestingDepthRelations);
	}

	/**
	 * Función para modificar las referencias de roles en organisation dentro de
	 * contactos de activity en caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de organisationRoles antes y
	 *            después de ser modificado.
	 */

	public void updateContactOrganisationRoles(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, roleClassInReference, contactOrganisationRolesPropertyPath,
				nestingDepthRelations);
	}

	/**
	 * Función para modificar las referencias de roles en contactos dentro de
	 * plataformas de activity en caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de contactRoles antes y después
	 *            de ser modificado.
	 */

	public void updatePlatformContactRoles(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, roleClassInReference, platformContactRolesPropertyPath,
				nestingDepthRelations);
	}

	public String getRankId() {
		return rankId;
	}

	public void setRankId(String rankId) {
		this.rankId = rankId;
	}
}
