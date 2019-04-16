package es.redmic.es.maintenance.device.mapper;

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

import es.redmic.es.administrative.service.ContactESService;
import es.redmic.es.common.utils.DataMapperUtils;
import es.redmic.models.es.administrative.dto.ContactDTO;
import es.redmic.models.es.administrative.model.ContactCompact;
import es.redmic.models.es.maintenance.device.dto.CalibrationDTO;
import es.redmic.models.es.maintenance.device.model.Calibration;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class CalibrationESMapper extends CustomMapper<Calibration, CalibrationDTO> {

	@Autowired
	ContactESService contactService;

	@Override
	public void mapAtoB(Calibration a, CalibrationDTO b, MappingContext context) {

		if (a.getContact() != null)
			b.setContact(mapperFacade.map(a.getContact(), ContactDTO.class));
	}

	@Override
	public void mapBtoA(CalibrationDTO b, Calibration a, MappingContext context) {

		if (b.getContact() != null)
			a.setContact((ContactCompact) mapperFacade.newObject(b.getContact(), DataMapperUtils.getBaseType(),
				DataMapperUtils.getObjectFactoryContext(contactService)));
	}
}
