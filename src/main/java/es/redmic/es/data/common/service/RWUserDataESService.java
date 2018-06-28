package es.redmic.es.data.common.service;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.common.dto.BaseDTO;
import es.redmic.models.es.common.model.BaseES;

public abstract class RWUserDataESService<TModel extends BaseES<?>, TDTO extends BaseDTO<?>>
		extends RDataESService<TModel, TDTO> {

	private RWDataESRepository<TModel> repository;

	protected static String DELETE_EVENT = "DELETE";
	protected static String ADD_EVENT = "ADD";
	protected static String UPDATE_EVENT = "UPDATE";

	public RWUserDataESService(RWDataESRepository<TModel> repository) {
		super(repository);
		this.repository = repository;
	}

	public TModel save(TModel modelToIndex) {

		return repository.save(modelToIndex);
	}

	public TModel update(TModel modelToIndex) {

		TModel origin = findById(modelToIndex.getId().toString());
		if (origin != null) {
			return repository.update(modelToIndex);
		} else
			return save(modelToIndex);
	}

	public void delete(String id) {

		repository.delete(id);
	}
}
