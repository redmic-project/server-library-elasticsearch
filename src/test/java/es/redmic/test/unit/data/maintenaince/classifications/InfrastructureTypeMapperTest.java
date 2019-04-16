package es.redmic.test.unit.data.maintenaince.classifications;

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

import java.io.IOException;

import org.json.JSONException;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.models.es.maintenance.point.dto.InfrastructureTypeDTO;
import es.redmic.models.es.maintenance.point.model.InfrastructureType;
import es.redmic.test.unit.geodata.common.MapperTestUtil;

public class InfrastructureTypeMapperTest extends MapperTestUtil {


	String modelOutPath = "/data/maintenance/classification/model/classificationTypeWithRemark.json",
			dtoInPath = "/data/maintenance/classification/dto/classificationTypeWithRemark.json";
	
	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {
		
		mapperDtoToModel(dtoInPath, modelOutPath, InfrastructureTypeDTO.class, InfrastructureType.class);
	}
}
