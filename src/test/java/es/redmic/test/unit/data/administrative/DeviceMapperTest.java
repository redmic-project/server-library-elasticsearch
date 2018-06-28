package es.redmic.test.unit.data.administrative;

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

import es.redmic.es.administrative.service.DocumentESService;
import es.redmic.es.administrative.service.PlatformESService;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.maintenance.device.mapper.DeviceESMapper;
import es.redmic.es.maintenance.device.service.DeviceTypeESService;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.device.dto.DeviceDTO;
import es.redmic.models.es.maintenance.device.model.Device;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class DeviceMapperTest extends MapperTestUtil {
	
	@Mock
	DeviceTypeESService deviceTypeESService;

	@Mock
	PlatformESService platformESService;

	@Mock
	DocumentESService documentESService;
	
	@InjectMocks
	DeviceESMapper mapper;

	String modelOutPath = "/data/administrative/device/model/device.json",
			dtoInPath = "/data/administrative/device/dto/device.json",
			deviceTypeModel = "/data/maintenance/model/domain.json",
			platformModel = "/data/administrative/platform/model/platform.json",
			documentModel = "/data/administrative/document/model/document.json";
	
	@Before
	public void setupTest() throws IOException {
		
		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);
		
		DomainES deviceType = (DomainES) getBean(deviceTypeModel, DomainES.class);	
		when(deviceTypeESService.findById(anyString())).thenReturn(deviceType);
		
		Platform platform = (Platform) getBean(platformModel, Platform.class);	
		when(platformESService.findById(anyString())).thenReturn(platform);
		
		Document document = (Document) getBean(documentModel, Document.class);	
		when(documentESService.findById(anyString())).thenReturn(document);
	}
	
	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {
		
		mapperDtoToModel(dtoInPath, modelOutPath, DeviceDTO.class, Device.class);
	}
}