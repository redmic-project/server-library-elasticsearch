package es.redmic.es.atlas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.atlas.repository.ThemeInspireESRepository;
import es.redmic.es.common.service.MetaDataESService;
import es.redmic.models.es.atlas.dto.ThemeInspireDTO;
import es.redmic.models.es.atlas.model.ThemeInspire;
import es.redmic.models.es.common.model.ReferencesES;

@Service
public class ThemeInspireESService extends MetaDataESService<ThemeInspire, ThemeInspireDTO> {

	@Autowired
	LayerESService layerESService;
	
	@Autowired
	public ThemeInspireESService(ThemeInspireESRepository repository) {
		super(repository);
	}

	@Override
	protected void postUpdate(ReferencesES<ThemeInspire> reference) {
		layerESService.updateThemeInspire(reference);
	}
}