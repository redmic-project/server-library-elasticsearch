package es.redmic.es.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import es.redmic.es.common.repository.HierarchicalESRepository;
import es.redmic.models.es.common.dto.DTO;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.BaseHierarchicalAbstractES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.utils.HierarchicalUtils;

public abstract class HierarchicalESService<TModel extends BaseHierarchicalAbstractES, TDTO extends DTO>
		extends MetaDataESService<TModel, TDTO> {

	HierarchicalESRepository<TModel, TDTO> repository;

	public HierarchicalESService(HierarchicalESRepository<TModel, TDTO> repository) {
		super(repository);
		this.repository = repository;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONCollectionDTO find(DataQueryDTO query) {

		JSONCollectionDTO result = super.find(query);

		// Si no existen resultados
		if (result.getTotal() == 0)
			return result;

		// Se garantiza devolver los hijos si el padre está en el resultado
		// o el padre si al menos un hijo está en el resultado.

		List<TDTO> hits = result.getData();

		Integer level = null;

		if (query.getTerms() != null && query.getTerms().containsKey("parentPath")) {
			String path = (String) query.getTerms().get("parentPath");
			level = path.split("\\.").length;
		}

		HashSet<String> ids = new HashSet<String>(), paths = new HashSet<String>(),
				presentPaths = new HashSet<String>();

		for (int i = 0; i < hits.size(); i++) {

			TDTO item = hits.get(i);
			BaseHierarchicalAbstractES hierarchicalFields = getHierarchicalFieldsFromDTO(item);
			presentPaths.add(hierarchicalFields.getPath().toString());
			// Si tiene padre
			if (hierarchicalFields.getPath().split("\\.").length > 2) {
				List<String> ancestorsPath = level == null
						? HierarchicalUtils.getAncestorsPaths(hierarchicalFields.getPath())
						: HierarchicalUtils.getAncestorsPathsByLevel(hierarchicalFields.getPath(), level);
				if (ancestorsPath != null)
					paths.addAll(ancestorsPath);
			}
			// Si tiene hijos
			if (hierarchicalFields.getLeaves() > 0) {
				ids.add(hierarchicalFields.getPath());
			}
		}

		int parentsCount = ids.size();
		if (!query.queryIsEmpty()) {

			Map<String, Object> terms = new HashMap<String, Object>();
			DataQueryDTO queryDTO = new DataQueryDTO();
			queryDTO.setSize(100);
			queryDTO.setDataType(query.getDataType());
			
			if (ids != null && ids.size() > 0) {
				
				terms.put("ids", new ArrayList<String>(ids));
			} else if (paths != null && paths.size() > 0) {
			
				terms.put("paths", new ArrayList<String>(paths));
			}
			if (!terms.isEmpty()) {
				
				queryDTO.setTerms(terms);
				terms.put("presentPaths", new ArrayList<String>(presentPaths));
				
				List<TDTO> newHits = find(queryDTO).getData();
				
				for (int i = 0; i < newHits.size(); i++) {
					BaseHierarchicalAbstractES hierarchicalFields = getHierarchicalFieldsFromDTO(newHits.get(i));
					if (hierarchicalFields.getLeaves() > 0)
						parentsCount++;
				}
				result.addAllData(newHits);
			}
		}
		// Total = only children
		result.setTotal(result.getData().size() - parentsCount);
		return result;
	}

	protected BaseHierarchicalAbstractES getHierarchicalFieldsFromDTO(TDTO item) {

		return orikaMapper.getMapperFacade().map(item, BaseHierarchicalAbstractES.class);
	}

	@Override
	public TModel update(TModel modelToIndex) {

		TModel origin = findById(modelToIndex.getId().toString());

		if (origin != null) {
			modelToIndex.setLeaves(origin.getLeaves());
			TModel target = repository.update(modelToIndex);
			transactUpdate(origin, target);
			return target;
		} else
			return save(modelToIndex);

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void postSave(Object model) {

		super.postSave(model);

		TModel obj = (TModel) model;

		Integer leaves = obj.getLeaves();
		if (leaves == null || leaves == 0)
			leaves = 1;
		// Se incrementa el número de hojas movidas a los nuevos ancestors
		increaseLeavesAncestors(obj.getPath(), leaves);
	}

	@Override
	protected void postUpdate(ReferencesES<TModel> reference) {

		if (!reference.getNewModel().getPath().equals(reference.getOldModel().getPath())) {

			Integer leaves = reference.getNewModel().getLeaves();
			if (leaves == null || leaves == 0)
				leaves = 1;

			updatePathChildren(reference.getOldModel().getPath(), reference.getNewModel().getPath());

			// Se disminuye el número de hojas movidas a los antiguos ancestors
			decreaseLeavesAncestors(reference.getOldModel().getPath(), leaves);
			// Se incrementa el número de hojas movidas a los nuevos ancestors
			increaseLeavesAncestors(reference.getNewModel().getPath(), leaves);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void preDelete(Object model) {

		TModel obj = (TModel) model;
		List<String> paths = new ArrayList<>();
		paths.add(obj.getPath());
		// Elimina recursivamente los hijos
		List<String> descendants = getDescendantsPaths(paths);
		for (int i = 0; i < descendants.size(); i++) {
			String[] pathSplit = descendants.get(i).split("\\.");
			repository.delete(pathSplit[pathSplit.length - 1]);
		}

		Integer leaves = obj.getLeaves();
		leaves++;

		decreaseLeavesAncestors(obj.getPath(), leaves);
	}

	@SuppressWarnings("unchecked")
	public List<String> getDescendantsPaths(List<String> parentsPath) {

		int size = parentsPath.size();
		List<String> paths = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			String[] parentSplit = parentsPath.get(i).split("\\.");
			paths.add(parentSplit[parentSplit.length - 1]);
		}

		List<TModel> result = (List<TModel>) repository.findByAscendants(paths).getSourceList();

		List<String> descendantsPaths = new ArrayList<String>();
		int resultSize = result.size();
		for (int i = 0; i < resultSize; i++) {
			String path = result.get(i).getPath().toString();
			// Descarta el path del padre para devolver los descendientes
			if (!parentsPath.contains(path))
				descendantsPaths.add(path);
		}

		return descendantsPaths;
	}

	@SuppressWarnings("unchecked")
	protected void increaseLeavesAncestors(String path, Integer numLeaves) {

		String[] pathSplitted = HierarchicalUtils.getAncestorsIds(path);

		if (pathSplitted != null) {

			for (int i = pathSplitted.length - 1; i >= 0; i--) {
				TModel ancestor = (TModel) repository.findById(pathSplitted[i]).get_source();
				if (ancestor != null) {
					Integer ancestorLeaves = ancestor.getLeaves();
					if (ancestorLeaves == null)
						ancestorLeaves = 0;

					ancestor.setLeaves(ancestorLeaves + numLeaves);
					repository.update(ancestor);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void decreaseLeavesAncestors(String path, Integer numLeaves) {

		String[] pathSplitted = HierarchicalUtils.getAncestorsIds(path);

		if (pathSplitted != null) {

			for (int i = pathSplitted.length - 1; i >= 0; i--) {
				TModel ancestor = (TModel) repository.findById(pathSplitted[i]).get_source();
				if (ancestor != null) {
					if (ancestor.getLeaves() >= numLeaves)
						ancestor.setLeaves(ancestor.getLeaves() - numLeaves);
					else
						ancestor.setLeaves(0);
					repository.update(ancestor);
				}
			}
		}
	}

	protected void updatePathChildren(String oldPath, String newPath) {

		String[] pathSplitted = oldPath.split("\\.");
		String id = pathSplitted[pathSplitted.length - 1];

		List<TModel> children = repository.getChildren(id);
		for (int i = 0; i < children.size(); i++) {
			TModel child = children.get(i);
			child.setPath(child.getPath().replace(oldPath, newPath));
			repository.update(child);
		}
	}

	public String getRankId() {
		return null;
	}

	public void setRankId(String rankId) {
	}

}
