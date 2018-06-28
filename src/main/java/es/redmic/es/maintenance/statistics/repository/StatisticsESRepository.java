package es.redmic.es.maintenance.statistics.repository;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.redmic.es.administrative.repository.ActivityESRepository;
import es.redmic.es.administrative.repository.ContactESRepository;
import es.redmic.es.administrative.repository.DocumentESRepository;
import es.redmic.es.administrative.repository.OrganisationESRepository;
import es.redmic.es.administrative.repository.PlatformESRepository;
import es.redmic.es.administrative.repository.ProgramESRepository;
import es.redmic.es.administrative.repository.ProjectESRepository;
import es.redmic.models.es.maintenance.statistics.dto.ResultDTO;

@Repository
public class StatisticsESRepository {

	@Autowired
	ProgramESRepository programESRepository;

	@Autowired
	ProjectESRepository projectESRepository;

	@Autowired
	ActivityESRepository activityESRepository;

	@Autowired
	OrganisationESRepository organisationESRepository;

	@Autowired
	ContactESRepository contactESRepository;

	@Autowired
	PlatformESRepository platformESRepository;

	@Autowired
	DocumentESRepository documentESRepository;
	
	private BoolQueryBuilder matchAllQuery = QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery());

	public ResultDTO programsStatistics() {

		BoolQueryBuilder query = QueryBuilders.boolQuery().filter(QueryBuilders.existsQuery("endDate"));

		ResultDTO result = new ResultDTO();
		result.setClose(programESRepository.getCount(query));
		result.setOpen(programESRepository.getCount(matchAllQuery) - result.getClose());
		return result;
	}

	public ResultDTO projectsStatistics() {

		BoolQueryBuilder query = QueryBuilders.boolQuery().filter(QueryBuilders.existsQuery("endDate"));

		ResultDTO result = new ResultDTO();
		result.setClose(projectESRepository.getCount(query));
		result.setOpen(projectESRepository.getCount(matchAllQuery) - result.getClose());
		return result;
	}

	public ResultDTO projectOutProgramStatistics() {

		ExistsQueryBuilder filter = QueryBuilders.existsQuery("endDate");
		QueryBuilder parent = QueryBuilders.termQuery("path.split", "7");

		BoolQueryBuilder queryOpen = QueryBuilders.boolQuery().filter(QueryBuilders.boolQuery().must(parent)),
				queryClose = QueryBuilders.boolQuery().filter(QueryBuilders.boolQuery().must(parent).must(filter));

		ResultDTO result = new ResultDTO();
		result.setClose(projectESRepository.getCount(queryClose));
		result.setOpen(projectESRepository.getCount(queryOpen));
		return result;
	}

	public ResultDTO activitiesStatistics() {

		BoolQueryBuilder query = QueryBuilders.boolQuery().filter(QueryBuilders.existsQuery("endDate"));

		ResultDTO result = new ResultDTO();
		result.setClose(activityESRepository.getCount(query));
		result.setOpen(activityESRepository.getCount(matchAllQuery) - result.getClose());
		return result;
	}

	public ResultDTO activityOutProjectStatistics() {

		ExistsQueryBuilder filter = QueryBuilders.existsQuery("endDate");
		QueryBuilder parent = QueryBuilders.termQuery("path.split", "25");

		BoolQueryBuilder queryOpen = QueryBuilders.boolQuery().filter(QueryBuilders.boolQuery().must(parent)),
				queryClose = QueryBuilders.boolQuery().filter(QueryBuilders.boolQuery().must(parent).must(filter));

		ResultDTO result = new ResultDTO();
		result.setClose(activityESRepository.getCount(queryClose));
		result.setOpen(activityESRepository.getCount(queryOpen));
		return result;
	}

	public Integer organisationStatistics() {

		return organisationESRepository.getCount(QueryBuilders.boolQuery());
	}

	public Integer contactsStatistics() {

		return contactESRepository.getCount(QueryBuilders.boolQuery());
	}

	public Integer platformsStatistics() {

		return platformESRepository.getCount(QueryBuilders.boolQuery());
	}

	public Integer documentsStatistics() {

		return documentESRepository.getCount(QueryBuilders.boolQuery());
	}
}
