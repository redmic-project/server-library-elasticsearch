package es.redmic.es.administrative.taxonomy.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.redmic.es.config.OrikaScanBeanESItfc;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.TaxonRankESService;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.exception.elasticsearch.ESInsertException;
import es.redmic.exception.taxonomy.InvalidAphiaException;
import es.redmic.exception.utils.WormsUpdateException;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.dto.WormsClassificationDTO;
import es.redmic.models.es.administrative.taxonomy.dto.WormsDTO;
import es.redmic.models.es.administrative.taxonomy.dto.WormsListDTO;
import es.redmic.models.es.maintenance.taxonomy.dto.RankDTO;
import es.redmic.utils.httpclient.HttpClient;

@Service
public class WormsToRedmicService {

	@Value("${property.path.worms.APHIA_RECORD_BY_APHIAID}")
	private String APHIA_RECORD_BY_APHIAID;

	@Value("${property.path.worms.APHIA_RECORDS_BY_NAME}")
	private String APHIA_RECORDS_BY_NAME;

	@Value("${property.path.worms.APHIA_CLASSIFICATION_BY_APHIAID}")
	private String APHIA_CLASSIFICATION_BY_APHIAID;

	@Autowired
	protected OrikaScanBeanESItfc orikaMapper;

	@Autowired
	TaxonESService taxonESService;

	@Autowired
	TaxonRankESService rankESService;

	@Autowired
	TaxonServiceGatewayItfc taxonService;

	HttpClient client = new HttpClient();

	private final String ROOT_RANK = "Kingdom";

	private List<RankDTO> ranks;

	private List<String> speciesRankLabel = new ArrayList<>();

	private final Logger LOGGER = LoggerFactory.getLogger(WormsToRedmicService.class);

	public WormsToRedmicService() {

	}

	@PostConstruct
	private void init() {
		ranks = rankESService.getRankClassification();

		for (RankDTO rank : ranks) {
			if (rank.getId() >= 10) {
				speciesRankLabel.add(rank.getName_en());
			}
		}
	}

	/*
	 * Dado un aphia seleccionado por el usuario se devuelve el taxonDTO con los
	 * datos de worms. NO GUARDA NADA
	 */

	public TaxonDTO wormsToRedmic4Update(Integer aphia) {

		LOGGER.info("Intentando obtener un taxón relleno de worms para su actualización. Aphia: " + aphia);

		WormsDTO wormsDTO = getAphiaRecordByAphiaId(aphia);

		checkIfRankIsInRedmic(wormsDTO);

		return processTaxon(orikaMapper.getMapperFacade().map(wormsDTO, TaxonDTO.class), wormsDTO.getValidAphia());
	}

	/*
	 * Se usa para la acualización individual. Guarda el taxón modificada
	 */

	public TaxonDTO update(String taxonId) {

		TaxonDTO taxonDTO = taxonESService.get(taxonId);
		return update(taxonDTO);
	}

	/*
	 * Dado un taxón almacenado en redmic, actualiza o nó de worms dependiendo de
	 * las restricciones
	 */

	public TaxonDTO update(TaxonDTO taxon) {

		LOGGER.info("Petición para actualizar taxón con Id: " + taxon.getId() + " scientificName: "
				+ taxon.getScientificName() + " aphia: " + taxon.getAphia());

		TaxonDTO taxonToUpdate = processUpdate(taxon);

		if (taxonToUpdate == null)
			return taxon;

		LOGGER.info("Modificando taxón con Id: " + taxon.getId() + " scientificName: " + taxon.getScientificName()
				+ " aphia: " + taxon.getAphia());

		return taxonService.update(taxonToUpdate);
	}

	// Si ha sido cambiado en worms, actualiza el taxón y las referencias a
	// otros taxones.
	public TaxonDTO processUpdate(TaxonDTO taxon) {

		if (taxon.getAphia() == null)
			return null;

		WormsDTO wormsDTO = getAphiaRecordByAphiaId(taxon.getAphia());

		if (wormsDTO == null) {
			LOGGER.error("El taxón que se está intentando actualizar tiene un aphia que no existe en worms. Id : "
					+ taxon.getId() + " scientificName: " + taxon.getScientificName() + " aphia: " + taxon.getAphia());

			throw new InvalidAphiaException(taxon.getAphia().toString());
		}

		checkIfRankIsInRedmic(wormsDTO);

		if (taxon.getWormsUpdated() != null
				&& taxon.getWormsUpdated().toLocalDateTime().isEqual(wormsDTO.getModified().toLocalDateTime())) {

			LOGGER.info("El taxón ya está actializado. Id : " + taxon.getId() + " scientificName: "
					+ taxon.getScientificName() + " aphia: " + taxon.getAphia());
			return null;
		}

		orikaMapper.getMapperFacade().map(wormsDTO, taxon);

		return processTaxon(taxon, wormsDTO.getValidAphia());
	}

	/*
	 * Devuelve el taxón buscado en worms, en un dto de redmic.
	 * 
	 */
	public TaxonDTO wormsToRedmic(Integer aphia) {

		LOGGER.info("Intentando obtener un taxón relleno de worms para insertarlo en REDMIC. Aphia: " + aphia);

		checkIfAphiaIsInRedmic(aphia);

		WormsDTO wormsDTO = getAphiaRecordByAphiaId(aphia);

		WormsClassificationDTO classification = getAphiaClassificationByAphiaId(wormsDTO.getAphia());

		checkIfScientificNameIsInRedmic(wormsDTO, classification);

		// Si el rank no está en redmic, se busca el siguiente de la
		// clasificación que si lo esté

		while (!taxonRankInRedmic(wormsDTO.getRank())) {

			LOGGER.info("El taxón que se está intentando insertar tiene un rank que no se encuentra en REDMIC, Aphia : "
					+ wormsDTO.getAphia() + " scientificName: " + wormsDTO.getScientificname()
					+ ". Buscando el taxón con rank inmediatamente superior.");

			WormsClassificationDTO wormsParent = classification.getParentItemByAphia(wormsDTO.getAphia());

			if (wormsParent == null)
				throw new ESInsertException("rank", wormsDTO.getRank());

			wormsDTO = getAphiaRecordByAphiaId(wormsParent.getAphia());
		}

		return processTaxon(orikaMapper.getMapperFacade().map(wormsDTO, TaxonDTO.class), wormsDTO.getValidAphia());
	}

	private TaxonDTO processTaxon(TaxonDTO taxon, Integer validAphia) {

		taxon = processAncestors(taxon);
		taxon = processValidAs(taxon, validAphia);

		return taxon;
	}

	private void checkIfAphiaIsInRedmic(Integer aphia) {

		TaxonDTO taxonSaved = taxonESService.findByAphia(aphia);

		if (taxonSaved != null) {
			LOGGER.error("El taxón que se está intentando insertar ya se encuentra en REDMIC. Id : "
					+ taxonSaved.getId() + " scientificName: " + taxonSaved.getScientificName() + " aphia: "
					+ taxonSaved.getAphia());
			throw new ESInsertException("aphia", aphia.toString());
		}
	}

	private void checkIfScientificNameIsInRedmic(WormsDTO wormsDTO, WormsClassificationDTO classification) {

		WormsClassificationDTO wormsParent = classification.getParentItemByAphia(wormsDTO.getAphia());

		if (wormsParent == null)
			return;

		TaxonDTO taxonSaved = taxonESService.findByScientificNameRankStatusAndParent(wormsDTO.getScientificname(),
				wormsDTO.getRank(), wormsDTO.getStatus(), wormsParent.getAphia());

		if (taxonSaved != null) {
			LOGGER.error(
					"El taxón que se está intentando insertar ya se encuentra en REDMIC, pero no tiene aphia. Id : "
							+ taxonSaved.getId() + " scientificName: " + taxonSaved.getScientificName());
			throw new ESInsertException("scientificName", wormsDTO.getScientificname());
		}
	}

	private void checkIfRankIsInRedmic(WormsDTO wormsDTO) {

		if (!taxonRankInRedmic(wormsDTO.getRank())) {
			LOGGER.error(
					"El taxón que se está intentando actualizar tiene un rank en worms que no existe en REDMIC. ScientificName: "
							+ wormsDTO.getScientificname() + " aphia: " + wormsDTO.getAphia());

			throw new WormsUpdateException(wormsDTO.getAphia().toString());
		}
	}

	private boolean taxonRankInRedmic(String rank) {

		if (rank == null)
			return false;

		for (RankDTO rankDTO : ranks)
			if (rankDTO.getName_en().equals(rank))
				return true;
		return false;
	}

	/*
	 * Busca el padre a partir de la clasificación y en caso de no existir sus
	 * ancestors en redmic, se guardan.
	 */
	private TaxonDTO processAncestors(TaxonDTO taxon) {

		WormsClassificationDTO classification = getAphiaClassificationByAphiaId(taxon.getAphia());

		String rank = taxon.getRank().getName_en();

		if (!rank.equalsIgnoreCase(ROOT_RANK)) {
			taxon.setParent(getParent(taxon.getAphia(), rank, classification, ranks));
		}
		return taxon;
	}

	/*
	 * A partir del aphia de la specie, el rank que proporciona worms y la
	 * classificación, se obtiene el padre
	 */
	private TaxonDTO getParent(Integer aphia, String rankLabel, WormsClassificationDTO classification,
			List<RankDTO> ranks) {

		RankDTO rank = getParentRank(rankLabel, ranks);

		String parentRankLabel = rank.getName_en();

		WormsClassificationDTO wormsParent = classification.getItemByRank(parentRankLabel);

		// Si se salta la jerarquía de rangos, se busca por el siguiente
		if (wormsParent == null)
			return getParent(aphia, parentRankLabel, classification, ranks);

		TaxonDTO parent = taxonESService.findByAphia(wormsParent.getAphia());

		if (parent == null) {
			LOGGER.info("Es necesario guardar un nuevo taxón, padre del taxón que queremos almacenar. ScientificName: "
					+ wormsParent.getScientificname() + " aphia: " + wormsParent.getAphia());
			return saveTaxonFromWorms(wormsParent.getAphia());
		}

		return parent;
	}

	/*
	 * En caso de no ser un taxón válido, se garda el mismo en el dto. Si no está en
	 * redmic se guarda a partir de worms
	 */
	private TaxonDTO processValidAs(TaxonDTO taxon, Integer validAphia) {

		if (validAphia == null || validAphia.equals(taxon.getAphia())) {
			return taxon;
		}

		TaxonDTO validTaxon = taxonESService.findByAphia(validAphia);

		// Si el taxón no está en redmic, se guarda
		if (validTaxon == null) {
			LOGGER.info("Es necesario guardar un nuevo taxón, taxón válido del taxón que queremos almacenar. Aphia: "
					+ validAphia);
			validTaxon = saveTaxonFromWorms(validAphia);
		}

		taxon.setValidAs(validTaxon);
		return taxon;
	}

	/* Se guarda los ancestors que no existen en la base de datos */

	private TaxonDTO saveTaxonFromWorms(Integer parentAphia) {

		TaxonDTO taxon = wormsToRedmic(parentAphia);

		// Si el aphia buscado coincide con el obtenido, se guarda
		if (taxon.getAphia() == parentAphia) {
			return taxonService.save(taxon);
		}
		// Si no, significa que que el buscado tenía rank que no está en redmic
		// y se busca el ancestro más próximo que si lo esté
		else {
			// Hay que comprobar si existe
			TaxonDTO savedTaxon = taxonESService.findByAphia(taxon.getAphia());
			if (savedTaxon != null) {
				// Si existe se retorna
				return savedTaxon;
			} else {
				// Si no existe se guarda
				return taxonService.save(taxon);
			}
		}
	}

	/*
	 * Se obtiene el rank de redmic que corresponde con el inmediatamente superior
	 * al obtenido de worms (Rank del padre)
	 * 
	 * EJ: Si rankLabel es Subphylum, se devueve Phylum
	 */

	private RankDTO getParentRank(String rankLabel, List<RankDTO> ranks) {
		int i = -1;
		for (i = ranks.size() - 1; i >= 0; i--) {
			// OJO, se debe usar "getName_en" para comparar con el ranklabel de
			// worms
			if (ranks.get(i).getName_en().toLowerCase().equals(rankLabel.toLowerCase())) {
				i--;
				break;
			}
		}

		if (i < 0)
			throw new ItemNotFoundException("rank", rankLabel);
		return ranks.get(i);
	}

	/*
	 * Obtiene un registro de worms a partir del aphia.
	 * 
	 */
	public WormsDTO getAphiaRecordByAphiaId(Integer aphia) {

		return (WormsDTO) client.get(APHIA_RECORD_BY_APHIAID + aphia, WormsDTO.class);
	}

	/*
	 * Obtiene registros de worms a partir del scientificname. Puede devolver varios
	 * resultados similares
	 * 
	 */

	public WormsListDTO findAphiaRecordsByScientificName(String scientificName) {

		WormsListDTO wormsList = (WormsListDTO) client.get(APHIA_RECORDS_BY_NAME + scientificName, WormsListDTO.class),
				result = new WormsListDTO();

		for (WormsDTO dto : wormsList) {

			if (speciesRankLabel.contains(dto.getRank())) {
				result.add(dto);
			}
		}

		return result != null ? result : new WormsListDTO();
	}

	/*
	 * Obtiene la clasificación de worms a partir del aphia
	 */
	public WormsClassificationDTO getAphiaClassificationByAphiaId(Integer aphia) {

		return (WormsClassificationDTO) client.get(APHIA_CLASSIFICATION_BY_APHIAID + aphia,
				WormsClassificationDTO.class);
	}
}
