package es.redmic.es.maintenance.domain.administrative.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.common.repository.DomainESRepository;
import es.redmic.models.es.maintenance.administrative.model.ActivityType;

@Repository
public class ActivityTypeESRepository extends DomainESRepository<ActivityType> {
	private static String[] INDEX = { "administrative-domains" };
	private static String[] TYPE = { "activitytype" };

	public ActivityTypeESRepository() {
		super(INDEX, TYPE);
	}
}
