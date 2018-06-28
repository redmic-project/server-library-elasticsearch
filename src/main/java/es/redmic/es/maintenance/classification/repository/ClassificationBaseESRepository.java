package es.redmic.es.maintenance.classification.repository;

import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import es.redmic.es.common.repository.HierarchicalESRepository;
import es.redmic.models.es.common.dto.BaseDTO;
import es.redmic.models.es.common.model.BaseES;

public abstract class ClassificationBaseESRepository<TModel extends BaseES<?>, TDTO extends BaseDTO<?>> extends HierarchicalESRepository<TModel, TDTO> {

	public ClassificationBaseESRepository() {
		super();
	}
	
	public ClassificationBaseESRepository(String[] index, String[] type) {
		super(index, type);
	}

	@Override
	public QueryBuilder getTermQuery(Map<String, Object> terms, BoolQueryBuilder query) {
		
		
		if (terms.containsKey("level")) {
			
			Integer level = (Integer) terms.get("level");
			query.filter(QueryBuilders.termQuery("level", level));
		}
		if (terms.containsKey("parentPath")) {
			
			String path = (String) terms.get("parentPath");
			Integer level = path.split("\\.").length;
			
			BoolQueryBuilder filter = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("path.path", path));
			
			Boolean returnChildren = true;
			if (terms.containsKey("children"))
				returnChildren = (Boolean) terms.get("children");
			
			if (returnChildren != null && !returnChildren)
				filter.must(QueryBuilders.termQuery("level", level));
			else
				filter.must(QueryBuilders.rangeQuery("level").gte(level));
			
			query.filter(filter);
		}
			
		return super.getTermQuery(terms, query);
	}
}