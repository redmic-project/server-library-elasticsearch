package es.redmic.es.series.objectcollecting.converter;

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
