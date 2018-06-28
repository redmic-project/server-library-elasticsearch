package es.redmic.es.common.queryFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import es.redmic.es.common.queryFactory.common.BaseQueryUtils;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

public final class QueryFactory {

	/*
	 * GetQuery para todo tipo de repositorios
	 */
	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		try {
			Class<? extends BaseQueryUtils> utils = QueryType.getUtils(queryDTO.getDataType());

			Method method = utils.getDeclaredMethod("getQuery", DataQueryDTO.class, QueryBuilder.class,
					QueryBuilder.class);

			Object[] params = new Object[] { queryDTO, internalQuery, partialQuery };

			return (BoolQueryBuilder) method.invoke(utils, params);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION, e);
		}
	}
}