package es.redmic.es.common.repository;

import org.springframework.stereotype.Repository;

import es.redmic.models.es.common.dto.SelectionDTO;
import es.redmic.models.es.common.model.Selection;

@Repository
public class SelectionRepository
		extends SettingsRepository<Selection, SelectionDTO> {

	private static String[] INDEX = { "user" };
	private static String[] TYPE = { "selection" };

	public SelectionRepository() {
		super(INDEX, TYPE);
	}
}
