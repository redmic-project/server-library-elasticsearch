package es.redmic.es.series.attributeseries.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.series.attributeseries.repository.AttributeSeriesESRepository;
import es.redmic.es.series.common.service.RWSeriesESService;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.series.attributeseries.dto.AttributeSeriesDTO;
import es.redmic.models.es.series.attributeseries.model.AttributeSeries;

@Service
public class AttributeSeriesESService extends RWSeriesESService<AttributeSeries, AttributeSeriesDTO> {

	@Autowired
	public AttributeSeriesESService(AttributeSeriesESRepository repository) {
		super(repository);
	}

	@Override
	public AttributeSeries mapper(AttributeSeriesDTO dtoToIndex) {
		return orikaMapper.getMapperFacade().map(dtoToIndex, AttributeSeries.class);
	}

	@Override
	protected void postUpdate(ReferencesES<AttributeSeries> reference) {}

	@Override
	protected void postSave(Object model) {}

	@Override
	protected void preDelete(Object object) {}

	@Override
	protected void postDelete(String id) {}
}