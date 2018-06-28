package es.redmic.test.unit.data.maintenaince;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.maintenance.parameter.mapper.UnitESMapper;
import es.redmic.es.maintenance.parameter.service.ParameterTypeESService;
import es.redmic.es.maintenance.parameter.service.UnitESService;
import es.redmic.es.maintenance.parameter.service.UnitTypeESService;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.parameter.dto.ParameterDTO;
import es.redmic.models.es.maintenance.parameter.model.Parameter;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class ParameterMapperTest extends MapperTestUtil {

	@Mock
	private ParameterTypeESService parameterTypeESService;

	@Mock
	private UnitESService unitESService;

	@Mock
	private UnitTypeESService unitTypeESService;

	@InjectMocks
	UnitESMapper mapper;

	String modelOutPath = "/data/maintenance/parameter/model/parameter.json",
			dtoInPath = "/data/maintenance/parameter/dto/parameter.json",
			unitModel = "/data/maintenance/unit/model/unit.json", domainModel = "/data/maintenance/model/domain.json";

	@Before
	public void setupTest() throws IOException {

		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);

		// DomainES parameterType = (DomainES) getBean(domainModel, DomainES.class);
		// when(parameterTypeESService.findById(anyString())).thenReturn(parameterType);

		// Unit unit = (Unit) getBean(unitModel, Unit.class);
		// when(unitESService.findById(anyString())).thenReturn(unit);

		DomainES unitType = (DomainES) getBean(domainModel, DomainES.class);
		when(unitTypeESService.findById(anyString())).thenReturn(unitType);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, ParameterDTO.class, Parameter.class);
	}
}