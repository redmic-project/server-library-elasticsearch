package es.redmic.es.geodata.tracking.animal.mapper;

/*-
 * #%L
 * ElasticSearch
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.redmic.es.administrative.taxonomy.service.AnimalESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.es.geodata.tracking.common.mapper.TrackingPropertiesESMapper;
import es.redmic.models.es.administrative.taxonomy.dto.AnimalCompactDTO;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.Animal;
import es.redmic.models.es.administrative.taxonomy.model.AnimalWithOutTaxon;
import es.redmic.models.es.geojson.properties.model.Collect;
import es.redmic.models.es.geojson.properties.model.GeoDataProperties;
import es.redmic.models.es.geojson.tracking.animal.dto.AnimalTrackingPropertiesDTO;
import ma.glasnost.orika.MappingContext;

@Component
public class AnimalTrackingPropertiesESMapper extends TrackingPropertiesESMapper<GeoDataProperties, AnimalTrackingPropertiesDTO> {

	@Autowired
	AnimalESService animalService;

	@Override
	public void mapAtoB(GeoDataProperties a, AnimalTrackingPropertiesDTO b, MappingContext context) {

		super.mapAtoB(a, b, context);

		b.setAnimal(mapperFacade.map(a.getCollect().getAnimal(), AnimalCompactDTO.class));
		b.getAnimal().setTaxon(mapperFacade.map(a.getCollect().getTaxon(), TaxonDTO.class));
		
		b.setRadius(a.getCollect().getRadius());
		b.setDeviation(a.getCollect().getDeviation());
	}

	@Override
	public void mapBtoA(AnimalTrackingPropertiesDTO b, GeoDataProperties a, MappingContext context) {

		super.mapBtoA(b, a, context);
		
		Collect collect = new Collect();
		
		collect.setZ(b.getZ());
		collect.setDeviation(b.getDeviation());
		collect.setRadius(b.getRadius());
		collect.setValue(1.0);
		collect.setvFlag(b.getvFlag());
		collect.setqFlag(b.getqFlag());

		Animal animal = (Animal) mapperFacade.newObject(b.getAnimal(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(animalService));
		collect.setAnimal(mapperFacade.map(animal, AnimalWithOutTaxon.class));
		collect.setTaxon(animal.getTaxon());

		a.setCollect(collect);
		a.setUpdated(b.getUpdated());
	}
}
