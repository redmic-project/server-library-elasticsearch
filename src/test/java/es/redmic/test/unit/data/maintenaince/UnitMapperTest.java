package es.redmic.test.unit.data.maintenaince;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.maintenance.parameter.mapper.UnitESMapper;
import es.redmic.es.maintenance.parameter.service.UnitTypeESService;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.parameter.dto.UnitDTO;
import es.redmic.models.es.maintenance.parameter.model.Unit;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class UnitMapperTest extends MapperTestUtil {
	
	@Mock
	private UnitTypeESService unitTypeESService;
	
	@InjectMocks
	UnitESMapper mapper;

	String modelOutPath = "/data/maintenance/unit/model/unit.json",
			dtoInPath = "/data/maintenance/unit/dto/unit.json",
			unitTypeModel = "/data/maintenance/model/domain.json";
	
	@Before
	public void setupTest() throws IOException {
		
		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);
		
		DomainES unitType = (DomainES) getBean(unitTypeModel, DomainES.class);	
		
		when(unitTypeESService.findById(anyString())).thenReturn(unitType);
	}
	
	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {
		
		mapperDtoToModel(dtoInPath, modelOutPath, UnitDTO.class, Unit.class);
	}
}