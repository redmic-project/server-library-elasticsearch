package es.redmic.es.maintenance.quality.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.maintenance.quality.repository.VFlagESRepository;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.quality.dto.VFlagDTO;
import es.redmic.models.es.maintenance.quality.model.VFlag;

@Service
public class VFlagESService extends MetaDataESService<VFlag,VFlagDTO> {

	@Autowired
	public VFlagESService(VFlagESRepository repository) {
		super(repository);
	}

	@Override
	protected void postUpdate(ReferencesES<VFlag> reference) {
		// TODO implementar postupdate	
	}
}
