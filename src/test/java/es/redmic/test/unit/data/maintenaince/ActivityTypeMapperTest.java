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
import es.redmic.es.maintenance.domain.administrative.mapper.ActivityTypeESMapper;
import es.redmic.es.maintenance.domain.administrative.service.ActivityFieldESService;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.dto.ActivityTypeDTO;
import es.redmic.models.es.maintenance.administrative.model.ActivityType;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class ActivityTypeMapperTest extends MapperTestUtil {
	
	@Mock
	ActivityFieldESService activityFieldService;
	
	@InjectMocks
	ActivityTypeESMapper mapper;

	String modelOutPath = "/data/maintenance/activitytype/model/activityType.json",
			dtoInPath = "/data/maintenance/activitytype/dto/activityType.json",
			activityFieldModel = "/data/maintenance/model/domain.json";
	
	@Before
	public void setupTest() throws IOException {
		
		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);
		
		DomainES activityField = (DomainES) getBean(activityFieldModel, DomainES.class);	
		
		when(activityFieldService.findById(anyString())).thenReturn(activityField);
	}
	
	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {
		
		mapperDtoToModel(dtoInPath, modelOutPath, ActivityTypeDTO.class, ActivityType.class);
	}
}