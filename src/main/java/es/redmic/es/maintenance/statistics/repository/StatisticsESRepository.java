package es.redmic.es.maintenance.statistics.repository;

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

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
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

	private static final String END_DATE_PROPERTY = "endDate";

	private static final String RANK_PROPERTY = "rank.id";

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

		TermQueryBuilder programQuery = QueryBuilders.termQuery(RANK_PROPERTY, 1);

		BoolQueryBuilder matchAllProgramsQuery = QueryBuilders.boolQuery().must(programQuery);

		BoolQueryBuilder closedQuery = QueryBuilders.boolQuery().must(programQuery)
			.must(QueryBuilders.existsQuery(END_DATE_PROPERTY));

		ResultDTO result = new ResultDTO();
		result.setClose(programESRepository.getCount(closedQuery));
		result.setOpen(programESRepository.getCount(matchAllProgramsQuery) - result.getClose());
		return result;
	}

	public ResultDTO projectsStatistics() {

		TermQueryBuilder projectQuery = QueryBuilders.termQuery(RANK_PROPERTY, 2);

		BoolQueryBuilder matchAllProjectsQuery = QueryBuilders.boolQuery().must(projectQuery);

		BoolQueryBuilder closedQuery = QueryBuilders.boolQuery().must(projectQuery)
			.must(QueryBuilders.existsQuery(END_DATE_PROPERTY));

		ResultDTO result = new ResultDTO();
		result.setClose(projectESRepository.getCount(closedQuery));
		result.setOpen(projectESRepository.getCount(matchAllProjectsQuery) - result.getClose());
		return result;
	}

	public ResultDTO projectOutProgramStatistics() {

		TermQueryBuilder projectQuery = QueryBuilders.termQuery(RANK_PROPERTY, 2);

		ExistsQueryBuilder filter = QueryBuilders.existsQuery(END_DATE_PROPERTY);

		QueryBuilder parent = QueryBuilders.termQuery("path.split", "7");

		BoolQueryBuilder queryOpen = QueryBuilders.boolQuery().must(projectQuery).must(parent);
		BoolQueryBuilder queryClose = QueryBuilders.boolQuery().must(projectQuery).must(parent).must(filter);

		ResultDTO result = new ResultDTO();
		result.setClose(projectESRepository.getCount(queryClose));
		result.setOpen(projectESRepository.getCount(queryOpen));
		return result;
	}

	public ResultDTO activitiesStatistics() {

		TermQueryBuilder activityQuery = QueryBuilders.termQuery(RANK_PROPERTY, 3);

		BoolQueryBuilder matchAllActivityQuery = QueryBuilders.boolQuery().must(activityQuery);

		BoolQueryBuilder closedQuery = QueryBuilders.boolQuery().must(activityQuery)
			.must(QueryBuilders.existsQuery(END_DATE_PROPERTY));

		ResultDTO result = new ResultDTO();
		result.setClose(activityESRepository.getCount(closedQuery));
		result.setOpen(activityESRepository.getCount(matchAllActivityQuery) - result.getClose());
		return result;
	}

	public ResultDTO activityOutProjectStatistics() {

		TermQueryBuilder activityQuery = QueryBuilders.termQuery(RANK_PROPERTY, 3);

		ExistsQueryBuilder filter = QueryBuilders.existsQuery(END_DATE_PROPERTY);

		QueryBuilder parent = QueryBuilders.termQuery("path.split", "25");

		BoolQueryBuilder queryOpen = QueryBuilders.boolQuery().must(activityQuery).must(parent);

		BoolQueryBuilder queryClose = QueryBuilders.boolQuery().must(activityQuery).must(parent).must(filter);

		ResultDTO result = new ResultDTO();
		result.setClose(activityESRepository.getCount(queryClose));
		result.setOpen(activityESRepository.getCount(queryOpen));
		return result;
	}

	public Integer organisationStatistics() {

		return organisationESRepository.getCount(matchAllQuery);
	}

	public Integer platformsStatistics() {

		return platformESRepository.getCount(matchAllQuery);
	}

	public Integer documentsStatistics() {

		return documentESRepository.getCount(matchAllQuery);
	}
}
