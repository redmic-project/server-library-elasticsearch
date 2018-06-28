package es.redmic.es.common.repository;

import java.util.List;

import org.elasticsearch.search.SearchHit;

public interface IProcessItemFunction<T> {

	void process(SearchHit hit);
	List<?> getResults();
	
}
