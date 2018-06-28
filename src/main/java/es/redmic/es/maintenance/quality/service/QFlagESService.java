package es.redmic.es.maintenance.quality.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.MetaDataESService;
import es.redmic.es.maintenance.quality.repository.QFlagESRepository;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.maintenance.quality.dto.QFlagDTO;
import es.redmic.models.es.maintenance.quality.model.QFlag;

@Service
public class QFlagESService extends MetaDataESService<QFlag,QFlagDTO> {

	@Autowired
	public QFlagESService(QFlagESRepository repository) {
		super(repository);
	}

	@Override
	protected void postUpdate(ReferencesES<QFlag> reference) {
		// TODO implementar postupdate
	}
}
