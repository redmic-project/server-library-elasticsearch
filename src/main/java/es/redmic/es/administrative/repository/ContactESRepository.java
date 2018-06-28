package es.redmic.es.administrative.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.administrative.model.Contact;

@Repository
public class ContactESRepository extends RWDataESRepository<Contact> {

	private static String[] INDEX = { "administrative" };
	private static String[] TYPE = { "contact" };

	public ContactESRepository() {
		super(INDEX, TYPE);
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "firstName", "firstName.suggest", "surname", "surname.suggest", "fullname.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "firstName", "firstName.suggest", "surname", "surname.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "firstName", "surname", "fullname" };
	}
}
