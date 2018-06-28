package es.redmic.es.series.objectcollecting.converter;

import java.util.Map;

import ma.glasnost.orika.CustomConverter;

public abstract class ClassificationConverterBase<TModel, TDTO> extends CustomConverter<TModel, TDTO> {

	
	protected int getValue(Map<String,Object> stats) {

		Object sum = stats.get("sum");
		int value = 0;
		if (sum != null)
			value = (int) (double) stats.get("sum");
		
		return value;
	}
}
