package es.redmic.es.administrative.taxonomy.service;

/*-
 * #%L
 * ElasticSearch
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import es.redmic.es.administrative.taxonomy.repository.TaxonESRepository;
import es.redmic.es.common.repository.HierarchicalESRepository;
import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.geodata.citation.service.CitationESService;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonBaseDTO;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonWithOutParentDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.administrative.taxonomy.model.TaxonAncestorsCompact;
import es.redmic.models.es.common.dto.DTOEvent;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.utils.HierarchicalUtils;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public abstract class TaxonBaseESService<TModel extends Taxon, TDTO extends TaxonWithOutParentDTO>
		extends MetaDataESService<TModel, TDTO> {

	@Autowired
	TaxonESRepository baseRepository;

	@Autowired
	CitationESService citationESService;

	@Autowired
	MisidentificationESService misidentificationESService;

	@Autowired
	AnimalESService animalESService;

	/* Clase del modelo indexado en la referencia */
	private static Class<TaxonAncestorsCompact> validAsClassInReference = TaxonAncestorsCompact.class;
	/* Path de elastic para buscar por validAs en taxon */
	private String validAsPropertyPath = "validAs.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> statusClassInReference = DomainES.class;
	/* Path de elastic para buscar por status */
	private String statusPropertyPath = "status.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> rankClassInReference = DomainES.class;
	/* Path de elastic para buscar por rank */
	private String rankPropertyPath = "rank.id";

	@SuppressWarnings("unchecked")
	@Autowired
	public TaxonBaseESService(TaxonESRepository taxonRepository) {
		super((HierarchicalESRepository<TModel, TDTO>) taxonRepository);
	}

	public TaxonBaseESService(HierarchicalESRepository<TModel, TDTO> repository) {
		super(repository);
	}

	@Override
	public Boolean chkEventIsMine(Type typeOfTDTO, DTOEvent event) {

		return super.chkEventIsMine(typeOfTDTO, event) && chkIsMyRank((TaxonBaseDTO) event.getDto());
	}

	public Boolean chkIsMyRank(TaxonBaseDTO taxonBaseDTO) {

		String rankId = getRankId();

		if (rankId == null)
			return false;

		int serviceRankId = Integer.parseInt(rankId);
		if ((serviceRankId == taxonBaseDTO.getRank().getId())
				|| (serviceRankId == 10 && serviceRankId < taxonBaseDTO.getRank().getId()))
			return true;
		return false;
	}

	public TaxonDTO findByAphia(Integer aphia) {

		DataSearchWrapper<Taxon> result = baseRepository.findByAphia(aphia);
		if (result.getSourceList().size() > 0)
			return orikaMapper.getMapperFacade().map(result.getSourceList().get(0), TaxonDTO.class);
		return null;
	}

	public TaxonDTO findByScientificNameRankStatusAndParent(String scientificName, String rank, String status,
			Integer parentAphia) {

		TaxonDTO taxonParentDTO = findByAphia(parentAphia);

		if (taxonParentDTO == null)
			return null;

		DataSearchWrapper<Taxon> result;
		
		if (taxonParentDTO.getParent() != null) {
		
			result = baseRepository.findByScientificNameRankStatusAndParent(scientificName, rank,
				status, taxonParentDTO.getParent().getId());
		}
		else {
			result = baseRepository.findByScientificNameRankAndStatus(scientificName, rank, status);
		}

		if (result.getSourceList().size() > 0)
			return orikaMapper.getMapperFacade().map(result.getSourceList().get(0), TaxonDTO.class);
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TModel update(TModel modelToIndex) {

		TModel origin = findById(modelToIndex.getId().toString());
		if (origin != null) {
			modelToIndex.setLeaves(origin.getLeaves());
			TModel target = (TModel) baseRepository.update(modelToIndex);
			transactUpdate(origin, target);
			return target;
		} else
			return save(modelToIndex);

	}

	/**
	 * Función para modificar las referencias de taxon en su repositorio en caso
	 * de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de taxon antes y después de ser
	 *            modificado.
	 */

	public void updateTaxon(ReferencesES<TModel> reference) {

		updateReference(reference, validAsClassInReference, validAsPropertyPath);
	}

	/**
	 * Función para modificar las referencias de status en taxon en caso de ser
	 * necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de status antes y después de ser
	 *            modificado.
	 */

	public void updateStatus(ReferencesES<DomainES> reference) {

		updateReference(reference, statusClassInReference, statusPropertyPath);
	}

	/**
	 * Función para modificar las referencias de rank en taxon en caso de ser
	 * necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de rank antes y después de ser
	 *            modificado.
	 */

	public void updateTaxonRank(ReferencesES<DomainES> reference) {

		updateReference(reference, rankClassInReference, rankPropertyPath);
	}

	@Override
	public void postUpdate(ReferencesES<TModel> reference) {

		updateTaxon(reference);

		if (!reference.getNewModel().getPath().equals(reference.getOldModel().getPath())) {

			Integer countLeaves = reference.getOldModel().getLeaves();

			if ((reference.getNewModel().getRank().getId() >= 10L || reference.getOldModel().getRank().getId() >= 10L)
					&& reference.getOldModel().getLeaves() == 0) {
				countLeaves = 1;
			}

			// Se disminuye el número de hojas movidas a los antiguos ancestors
			decreaseLeavesAncestors(reference.getOldModel(), countLeaves);
			// Se incrementa el número de hojas movidas a los nuevos ancestors
			increaseLeavesAncestors(reference.getNewModel(), countLeaves);
		}
	}

	@Override
	public void postSave(Object taxon) {

		super.postSave(taxon);

		if (((Taxon) taxon).getRank().getId() >= 10L)
			increaseLeavesAncestors((Taxon) taxon, 1);
	}

	@Override
	public void preDelete(Object taxon) {

		super.preDelete(taxon);

		if (((Taxon) taxon).getRank().getId() >= 10L)
			decreaseLeavesAncestors((Taxon) taxon, 1);
	}

	// TODO: usar si se puede el de HierarchicalService
	private void increaseLeavesAncestors(Taxon taxon, Integer numLeaves) {

		String[] pathSplitted = HierarchicalUtils.getAncestorsIds(taxon.getPath());

		if (pathSplitted != null) {

			for (int i = pathSplitted.length - 1; i >= 0; i--) {
				Taxon ancestor = (Taxon) baseRepository.findById(pathSplitted[i]).get_source();
				ancestor.setLeaves(ancestor.getLeaves() + numLeaves);
				baseRepository.update(ancestor);
				if (ancestor.getId() >= 10L && ancestor.getLeaves() == 0) {
					return;
				}
			}
		}
	}

	// TODO: usar si se puede el de HierarchicalService
	private void decreaseLeavesAncestors(Taxon taxon, Integer numLeaves) {

		String[] pathSplitted = HierarchicalUtils.getAncestorsIds(taxon.getPath());

		if (pathSplitted != null) {

			for (int i = pathSplitted.length - 1; i >= 0; i--) {
				Taxon ancestor = (Taxon) baseRepository.findById(pathSplitted[i]).get_source();
				Integer oldLeaves = ancestor.getLeaves();
				ancestor.setLeaves(ancestor.getLeaves() - numLeaves);
				baseRepository.update(ancestor);
				if (ancestor.getRank().getId() >= 10L && oldLeaves == 1) {
					return;
				}
			}
		}
	}

	public Integer getCountLeaves(List<String> parentsPath) {

		if (parentsPath == null || parentsPath.size() == 0)
			return 0;

		int size = parentsPath.size();
		List<String> paths = new ArrayList<String>();

		for (int i = 0; i < size; i++) {

			String[] parentSplit = parentsPath.get(i).split("\\.");
			paths.add(parentSplit[parentSplit.length - 1]);
		}

		return baseRepository.findChildByAscendants(paths).getSourceList().size();
	}

	@SuppressWarnings("unchecked")
	public List<String> getDescendantsIds(List<String> parentsPath) {

		int size = parentsPath.size();
		List<String> paths = new ArrayList<String>();

		for (int i = 0; i < size; i++) {

			String[] parentSplit = parentsPath.get(i).split("\\.");
			paths.add(parentSplit[parentSplit.length - 1]);
		}

		List<TModel> result = (List<TModel>) baseRepository.findByAscendants(paths).getSourceList();

		List<String> childrenPaths = new ArrayList<String>();
		int resultSize = result.size();

		for (int i = 0; i < resultSize; i++) {
			String path = result.get(i).getPath().toString();
			childrenPaths.add(path);
		}

		return childrenPaths;
	}

	public String getRankId() {
		return null;
	}

	public void setRankId(String rankId) {
	}
}
