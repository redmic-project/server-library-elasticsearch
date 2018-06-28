package es.redmic.es.geodata.area.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.geodata.area.repository.AreaESRepository;
import es.redmic.es.geodata.common.service.GeoDataESService;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.geojson.area.dto.AreaDTO;
import es.redmic.models.es.geojson.common.model.GeoMultiPolygonData;

@Service
public class AreaESService extends GeoDataESService<AreaDTO, GeoMultiPolygonData> {

	AreaESRepository repository;

	@Autowired
	public AreaESService(AreaESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	@Override
	public GeoMultiPolygonData mapper(AreaDTO dtoToIndex) {

		return orikaMapper.getMapperFacade().map(dtoToIndex, GeoMultiPolygonData.class);
	}

	public AreaDTO findBySamplingId(String samplingId) {

		return orikaMapper.getMapperFacade().map(repository.findBySamplingId(samplingId), AreaDTO.class);
	}

	@Override
	protected void postUpdate(ReferencesES<GeoMultiPolygonData> reference) {
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
