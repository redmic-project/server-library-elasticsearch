package es.redmic.es.series.attributeseries.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.series.common.repository.RWSeriesESRepository;
import es.redmic.models.es.series.attributeseries.model.AttributeSeries;

@Repository
public class AttributeSeriesESRepository extends RWSeriesESRepository<AttributeSeries> {

	private static String[] INDEX = { "activity" };
	private static String[] TYPE = { "attributeseries" };
	
	public AttributeSeriesESRepository() {
		super(INDEX, TYPE);
	}
}
