package es.redmic.es.common.queryFactory;

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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import es.redmic.es.common.queryFactory.common.BaseQueryUtils;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;
import es.redmic.models.es.common.query.dto.MetadataQueryDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;

public final class QueryFactory {

	/*
	 * GetQuery para todo tipo de repositorios
	 */
	public static <TQueryDTO extends SimpleQueryDTO> BoolQueryBuilder getQuery(TQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		try {
			Class<? extends BaseQueryUtils> utils = QueryType.getUtils(queryDTO.getDataType());

			Method method;

			Class<?> classs;

			if (queryDTO instanceof GeoDataQueryDTO) {
				classs = GeoDataQueryDTO.class;
			}
			else if (queryDTO instanceof DataQueryDTO){
				classs = DataQueryDTO.class;
			}
			else if (queryDTO instanceof MetadataQueryDTO) {
				classs = MetadataQueryDTO.class;
			}
			else {
				classs = SimpleQueryDTO.class;
			}

			method = utils.getDeclaredMethod("getQuery", classs , QueryBuilder.class, QueryBuilder.class);

			Object[] params = new Object[] { queryDTO, internalQuery, partialQuery };

			return (BoolQueryBuilder) method.invoke(utils, params);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION, e);
		}
	}
}
