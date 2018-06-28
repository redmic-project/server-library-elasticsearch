package es.redmic.test.unit.data.maintenaince.classifications;

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