package es.redmic.es.administrative.repository;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.administrative.dto.PlatformDTO;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

@Repository
public class PlatformESRepository extends RWDataESRepository<Platform> {

	private static String[] INDEX = { "administrative" };
	private static String[] TYPE = { "platform" };

	public PlatformESRepository() {
		super(INDEX, TYPE);
	}

	@SuppressWarnings("unchecked")
	public PlatformDTO findByUuid(String uuid) {

		QueryBuilder query = QueryBuilders.termQuery("uuid", uuid);
		DataSearchWrapper<Platform> result = (DataSearchWrapper<Platform>) findBy(query);
		if (result == null)
			return null;
		return orikaMapper.getMapperFacade().map(result.getSource(0), PlatformDTO.class);
	}
}
