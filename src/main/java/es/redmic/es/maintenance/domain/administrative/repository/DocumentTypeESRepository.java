package es.redmic.es.maintenance.domain.administrative.repository;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Repository
public class DocumentTypeESRepository extends RWDataESRepository<DomainES> {
	private static String[] INDEX = { "administrative-domains" };
	private static String[] TYPE = { "documenttype" };

	public DocumentTypeESRepository() {
		super(INDEX, TYPE);
	}

	@SuppressWarnings("unchecked")
	public DataSearchWrapper<DomainES> findByName(String name) {

		QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();

		BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("name", name));

		return (DataSearchWrapper<DomainES>) findBy(QueryBuilders.boolQuery().must(queryBuilder).filter(filterBuilder));
	}
}
