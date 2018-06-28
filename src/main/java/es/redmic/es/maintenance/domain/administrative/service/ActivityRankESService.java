package es.redmic.es.maintenance.domain.administrative.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.common.service.DomainESService;
import es.redmic.es.maintenance.domain.administrative.repository.ActivityRankESRepository;
import es.redmic.models.es.common.dto.DomainDTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;

@Service
public class ActivityRankESService extends DomainESService<DomainES, DomainDTO> {

	@Autowired
	public ActivityRankESService(ActivityRankESRepository repository) {
		super(repository);
	}
	
	@Override
	protected void postUpdate(ReferencesES<DomainES> reference) {
		//TODO: implementar postUpdate
	}
}
