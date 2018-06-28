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

import es.redmic.es.administrative.mapper.ContactESMapper;
import es.redmic.es.administrative.service.OrganisationESService;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.models.es.administrative.dto.ContactDTO;
import es.redmic.models.es.administrative.model.Contact;
import es.redmic.models.es.administrative.model.Organisation;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class ContactMapperTest extends MapperTestUtil {
	
	@Mock
	OrganisationESService organisationESService;
	
	@InjectMocks
	ContactESMapper mapper;

	String modelOutPath = "/data/administrative/contact/model/contact.json",
			dtoInPath = "/data/administrative/contact/dto/contact.json",
			organisationModel = "/data/administrative/organisation/model/organisation.json";
	
	@Before
	public void setupTest() throws IOException {
		
		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);
		
		Organisation organisation = (Organisation) getBean(organisationModel, Organisation.class);	
		when(organisationESService.findById(anyString())).thenReturn(organisation);
	}
	
	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {
		
		mapperDtoToModel(dtoInPath, modelOutPath, ContactDTO.class, Contact.class);
	}
}