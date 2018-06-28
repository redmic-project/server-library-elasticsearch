package es.redmic.test.unit.data.maintenaince.classifications;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.models.es.maintenance.objects.dto.ObjectTypeDTO;
import es.redmic.models.es.maintenance.objects.model.ObjectType;
import es.redmic.test.unit.geodata.common.MapperTestUtil;

public class ObjectTypeMapperTest extends MapperTestUtil {


	String modelOutPath = "/data/maintenance/classification/model/classificationType.json",
			dtoInPath = "/data/maintenance/classification/dto/classificationType.json";
	
	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {
		
		mapperDtoToModel(dtoInPath, modelOutPath, ObjectTypeDTO.class, ObjectType.class);
	}
}