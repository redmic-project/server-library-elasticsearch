package es.redmic.test.unit.data.administrative.taxonomy.es2dto;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.JavaType;

import es.redmic.es.administrative.taxonomy.mapper.MisidentificationESMapper;
import es.redmic.es.data.common.mapper.DataCollectionMapper;
import es.redmic.es.data.common.mapper.DataItemMapper;
import es.redmic.es.geodata.citation.service.CitationESService;
import es.redmic.models.es.administrative.taxonomy.dto.MisidentificationDTO;
import es.redmic.models.es.administrative.taxonomy.model.Misidentification;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.models.es.geojson.citation.dto.CitationDTO;
import es.redmic.test.unit.geodata.common.JsonToBeanTestUtil;
import es.redmic.test.utils.OrikaScanBeanTest;
@RunWith(MockitoJUnitRunner.class)
public class MisidentificationSearchWrapperTest extends JsonToBeanTestUtil {

	protected OrikaScanBeanTest factory = new OrikaScanBeanTest();
	
	@Mock
	CitationESService citationService;
	
	@InjectMocks
	MisidentificationESMapper mapper;
	
	String modelInPath = "/data/administrative/taxonomy/misidentification/model/searchWrapperMisidentificationModel.json",
			dtoOuthPath = "/data/administrative/taxonomy/misidentification/dto/searchWrapperMisidentificationDTO.json",
			citationDTOListPath = "/data/administrative/taxonomy/misidentification/dto/citationList.json";

	@SuppressWarnings("unchecked")
	@Before
	public void setupTest() throws IOException {
		
		factory.addMapper(new DataCollectionMapper());
		factory.addMapper(new DataItemMapper());
		factory.addMapper(mapper);
		
		JavaType type = jacksonMapper.getTypeFactory().constructParametricType(List.class, CitationDTO.class);

		List<CitationDTO> citations = (List<CitationDTO>) getBean(citationDTOListPath, type);
		when(citationService.findByMisidentification(anyString())).thenReturn(citations);
	}
	
	@Test
	public void test() throws IOException, ClassNotFoundException, JSONException {
	
		JavaType type = jacksonMapper.getTypeFactory().constructParametricType(DataSearchWrapper.class, Misidentification.class);
		DataSearchWrapper<?> beanIn = (DataSearchWrapper<?>) getBean(modelInPath, type);

		Object beanOut = factory.getMapperFacade().map(beanIn.getHits(), JSONCollectionDTO.class, getContext(MisidentificationDTO.class));

		JSONAssert.assertEquals(jacksonMapper.writeValueAsString(beanOut), getJsonString(dtoOuthPath), true);
	}
}
