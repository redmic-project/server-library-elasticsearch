package es.redmic.test.unit.data.maintenaince.classifications;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.models.es.maintenance.qualifiers.dto.AttributeTypeDTO;
import es.redmic.models.es.maintenance.qualifiers.model.AttributeType;
import es.redmic.test.unit.geodata.common.MapperTestUtil;

public class AttributeTypeMapperTest extends MapperTestUtil {


	String modelOutPath = "/data/maintenance/classification/model/classificationType.json",
			dtoInPath = "/data/maintenance/classification/dto/classificationType.json";
	
	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {
		
		mapperDtoToModel(dtoInPath, modelOutPath, AttributeTypeDTO.class, AttributeType.class);
	}
}