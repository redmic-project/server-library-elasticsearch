package es.redmic.es.atlas.repository;

import org.springframework.stereotype.Repository;

import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.models.es.atlas.model.ThemeInspire;

@Repository
public class ThemeInspireESRepository extends RWDataESRepository<ThemeInspire> {

	private static String[] INDEX = { "atlas" };
	private static String[] TYPE = { "themeinspire" };

	public ThemeInspireESRepository() {
		super(INDEX, TYPE);
	}
}
