package es.redmic.es.administrative.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.administrative.repository.ActivityBaseESRepository;
import es.redmic.models.es.administrative.dto.ActivityBaseDTO;
import es.redmic.models.es.administrative.model.ActivityBase;
import es.redmic.models.es.common.model.ReferencesES;

@Service("ActivityBaseServiceES")
public class ActivityBaseESService extends ActivityBaseAbstractESService<ActivityBase, ActivityBaseDTO> {
	
	@Autowired
	public ActivityBaseESService(ActivityBaseESRepository repository) {
		super(repository);
	}

	@Override
	protected void postUpdate(ReferencesES<ActivityBase> reference) {}
}
