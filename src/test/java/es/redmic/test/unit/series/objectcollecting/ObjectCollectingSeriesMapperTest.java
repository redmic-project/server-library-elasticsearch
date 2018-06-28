package es.redmic.test.unit.series.objectcollecting;

import static org.mockito.Matchers.any;
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

import es.redmic.es.maintenance.domain.administrative.taxonomy.service.ConfidenceESService;
import es.redmic.es.maintenance.objects.service.ObjectTypeESService;
import es.redmic.es.series.objectcollecting.mapper.ObjectCollectingESMapper;
import es.redmic.es.series.objectcollecting.mapper.ObjectCollectingSeriesESMapper;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.objects.model.ObjectType;
import es.redmic.models.es.series.objectcollecting.dto.ObjectCollectingSeriesDTO;
import es.redmic.models.es.series.objectcollecting.model.ObjectCollectingSeries;
import es.redmic.test.unit.geodata.common.MapperTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class ObjectCollectingSeriesMapperTest extends MapperTestUtil {
	
	@Mock
	ConfidenceESService confidenceService;
	
	@Mock
	ObjectTypeESService objectService;
	
	@InjectMocks
	ObjectCollectingESMapper objectMapper;
	
	@InjectMocks
	ObjectCollectingSeriesESMapper mapper;

	String modelOutPath = "/geodata/objectcollecting/model/objectCollectingSeries.json",
			dtoInPath = "/geodata/objectcollecting/dto/objectCollectingSeriesIn.json",
			confidenceModel = "/geodata/common/model/confidence.json",
			classificationObjectGroup1 = "/geodata/objectcollecting/model/classificationObject1.json",
			classificationObjectGroup2 = "/geodata/objectcollecting/model/classificationObject2.json",
			classificationObjectGroup8 = "/geodata/objectcollecting/model/classificationObject8.json",
			classificationObjectGroup9 = "/geodata/objectcollecting/model/classificationObject9.json",
			classificationObjectGroup10 = "/geodata/objectcollecting/model/classificationObject10.json",
			classificationObjectGroup11 = "/geodata/objectcollecting/model/classificationObject11.json";
	
	@Before
	public void setupTest() throws IOException {
		
		factory.addMapper(objectMapper);
		factory.addMapper(mapper);
		
		
		DomainES confidence = (DomainES) getBean(confidenceModel, DomainES.class);
		
		ObjectType objectExpected1 = (ObjectType) getBean(classificationObjectGroup1, ObjectType.class);
		ObjectType objectExpected2 = (ObjectType) getBean(classificationObjectGroup2, ObjectType.class);
		ObjectType objectExpected8 = (ObjectType) getBean(classificationObjectGroup8, ObjectType.class);
		ObjectType objectExpected9 = (ObjectType) getBean(classificationObjectGroup9, ObjectType.class);
		ObjectType objectExpected10 = (ObjectType) getBean(classificationObjectGroup10, ObjectType.class);
		ObjectType objectExpected11 = (ObjectType) getBean(classificationObjectGroup11, ObjectType.class);
		
		when(confidenceService.findById(any(String.class))).thenReturn(confidence);
		
		when(objectService.findById("1")).thenReturn(objectExpected1);
		when(objectService.findById("2")).thenReturn(objectExpected2);
		when(objectService.findById("8")).thenReturn(objectExpected8);
		when(objectService.findById("9")).thenReturn(objectExpected9);
		when(objectService.findById("10")).thenReturn(objectExpected10);
		when(objectService.findById("11")).thenReturn(objectExpected11);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {
		
		mapperDtoToModel(dtoInPath, modelOutPath, ObjectCollectingSeriesDTO.class, ObjectCollectingSeries.class);
	}
}