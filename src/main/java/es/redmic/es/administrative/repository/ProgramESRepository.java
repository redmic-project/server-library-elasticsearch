package es.redmic.es.administrative.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.redmic.es.common.service.UserUtilsServiceItfc;
import es.redmic.models.es.administrative.model.Program;

@Repository
public class ProgramESRepository extends ActivityCommonESRepository<Program> {

	private static String[] INDEX = { "activity" };
	private static String[] TYPE = { "program" };

	@Autowired
	UserUtilsServiceItfc userService;

	public ProgramESRepository() {
		super(INDEX, TYPE);
	}
}