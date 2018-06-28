package es.redmic.es.administrative.taxonomy.repository;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.administrative.taxonomy.dto.AnimalDTO;
import es.redmic.models.es.administrative.taxonomy.model.Animal;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Repository
public class AnimalESRepository extends RWDataESRepository<Animal> {

	private static String[] INDEX = { "taxons" };
	private static String[] TYPE = { "animal" };

	public AnimalESRepository() {
		super(INDEX, TYPE);
	}

	@SuppressWarnings("unchecked")
	public AnimalDTO findByUuid(String uuid) {

		QueryBuilder query = QueryBuilders.termQuery("uuid", uuid);
		DataSearchWrapper<Animal> result = (DataSearchWrapper<Animal>) findBy(query);
		if (result == null)
			return null;
		return orikaMapper.getMapperFacade().map(result.getSource(0), AnimalDTO.class);
	}
}