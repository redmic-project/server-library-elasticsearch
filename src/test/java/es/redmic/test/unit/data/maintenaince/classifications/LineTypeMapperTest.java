package es.redmic.test.unit.data.maintenaince.classifications;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.models.es.maintenance.line.dto.LineTypeDTO;
import es.redmic.models.es.maintenance.line.model.LineType;
import es.redmic.test.unit.geodata.common.MapperTestUtil;

public class LineTypeMapperTest extends MapperTestUtil {


	String modelOutPath = "/data/maintenance/classification/model/classificationTypeWithRemark.json",
			dtoInPath = "/data/maintenance/classification/dto/classificationTypeWithRemark.json";
	
	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {
		
		mapperDtoToModel(dtoInPath, modelOutPath, LineTypeDTO.class, LineType.class);
	}
}