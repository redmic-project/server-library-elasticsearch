package es.redmic.es.common.service;

import java.util.List;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.es.data.common.service.RWDataESService;
import es.redmic.models.es.common.dto.DTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public abstract class DomainESService<TModel extends DomainES, TDTO extends DTO> extends RWDataESService<TModel, TDTO> {
	DomainESRepository<TModel> repository;

	public DomainESService(DomainESRepository<TModel> repository) {
		super(repository);
		this.repository = repository;
	}

	@SuppressWarnings("unchecked")
	public DomainES findByName(String name) {

		DataSearchWrapper<DomainES> registers = (DataSearchWrapper<DomainES>) repository.findByName(name);
		List<DomainES> sourceList = registers.getSourceList();
		if (sourceList.size() == 0)
			return null;

		return sourceList.get(0);
	}

	@SuppressWarnings("unchecked")
	public DomainES findByName_en(String name) {

		DataSearchWrapper<DomainES> registers = (DataSearchWrapper<DomainES>) repository.findByName_en(name);
		List<DomainES> sourceList = registers.getSourceList();
		if (sourceList.size() == 0)
			return null;

		return sourceList.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TModel mapper(TDTO dtoToIndex) {
		return (TModel) orikaMapper.getMapperFacade().map(dtoToIndex, DomainES.class);
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
