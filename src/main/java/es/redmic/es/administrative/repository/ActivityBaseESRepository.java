package es.redmic.es.administrative.repository;

import org.springframework.stereotype.Repository;

import es.redmic.models.es.administrative.model.ActivityBase;

@Repository
public class ActivityBaseESRepository extends ActivityCommonESRepository<ActivityBase> {

	private static String[] INDEX = { "activity" };
	private static String[] TYPE = { "activity", "project", "program" };

	public ActivityBaseESRepository() {
		super(INDEX, TYPE);
	}
}
