package es.redmic.test.unit.data.maintenaince;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.common.dto.AccessibilityDTO;
import es.redmic.test.unit.geodata.common.MapperTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class DomainsMapperTest extends MapperTestUtil {

	String modelOutPath = "/data/maintenance/model/domain.json",
			dtoInPath = "/data/maintenance/dto/domain.json";
	
	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {
		
		mapperDtoToModel(dtoInPath, modelOutPath, AccessibilityDTO.class, DomainES.class);
	}
}
