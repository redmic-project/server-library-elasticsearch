package es.redmic.test.unit.data.maintenaince.classifications;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.models.es.maintenance.areas.dto.ThematicTypeDTO;
import es.redmic.models.es.maintenance.areas.model.ThematicType;
import es.redmic.test.unit.geodata.common.MapperTestUtil;

public class ThematicTypeMapperTest extends MapperTestUtil {


	String modelOutPath = "/data/maintenance/classification/model/classificationTypeWithColour.json",
			dtoInPath = "/data/maintenance/classification/dto/classificationTypeWithColour.json";
	
	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {
		
		mapperDtoToModel(dtoInPath, modelOutPath, ThematicTypeDTO.class, ThematicType.class);
	}
}