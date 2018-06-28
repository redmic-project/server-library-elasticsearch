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

import es.redmic.es.administrative.mapper.DocumentESMapper;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.maintenance.domain.administrative.service.DocumentTypeESService;
import es.redmic.models.es.administrative.dto.DocumentDTO;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class DocumentMapperTest extends MapperTestUtil {

	@Mock
	DocumentTypeESService documentTypeESService;

	@InjectMocks
	DocumentESMapper mapper;

	String modelOutPath = "/data/administrative/document/model/document.json",
			dtoInPath = "/data/administrative/document/dto/document.json",
			documentTypeModel = "/data/maintenance/documenttype/model/documentType.json";

	@Before
	public void setupTest() throws IOException {

		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);

		DomainES documentType = (DomainES) getBean(documentTypeModel, DomainES.class);
		when(documentTypeESService.findById(anyString())).thenReturn(documentType);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, DocumentDTO.class, Document.class);
	}
}