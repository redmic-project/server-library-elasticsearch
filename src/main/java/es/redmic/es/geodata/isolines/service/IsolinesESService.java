package es.redmic.es.geodata.isolines.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.geodata.common.service.GeoDataWithSeriesESService;
import es.redmic.es.geodata.isolines.repository.IsolinesESRepository;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.geojson.common.model.GeoMultiLineStringData;
import es.redmic.models.es.geojson.isolines.dto.IsolinesDTO;

@Service
public class IsolinesESService extends GeoDataWithSeriesESService<IsolinesDTO, GeoMultiLineStringData> {
	
	@Autowired
	public IsolinesESService(IsolinesESRepository repository) {
		super(repository);
	}
	
	@Override
	public GeoMultiLineStringData mapper(IsolinesDTO dtoToIndex) {
		
		return orikaMapper.getMapperFacade().map(dtoToIndex, GeoMultiLineStringData.class);
	}

	@Override
	protected void postUpdate(ReferencesES<GeoMultiLineStringData> reference) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void postSave(Object model) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void preDelete(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void postDelete(String id) {
		// TODO Auto-generated method stub
		
	}
}
