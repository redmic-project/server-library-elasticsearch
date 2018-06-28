package es.redmic.es.common.service;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.es.data.common.service.RWDataESService;
import es.redmic.models.es.common.dto.BaseDTO;
import es.redmic.models.es.common.model.BaseES;

public abstract class MetaDataESService<TModel extends BaseES<?>, TDTO extends BaseDTO<?>>
		extends RWDataESService<TModel, TDTO> {

	RWDataESRepository<TModel> repository;

	public MetaDataESService(RWDataESRepository<TModel> repository) {
		super(repository);
		this.repository = repository;
	}

	@Override
	public TModel mapper(TDTO dtoToIndex) {
		return orikaMapper.getMapperFacade().map(dtoToIndex, typeOfTModel);
	}

	@Override
	protected void postSave(Object model) {
	}

	@Override
	protected void preDelete(Object object) {
	}

	@Override
	protected void postDelete(String id) {
	}
}
