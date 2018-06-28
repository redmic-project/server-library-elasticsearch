package es.redmic.test.unit.geodata.area;

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

import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.geodata.area.mapper.AreaESMapper;
import es.redmic.es.geodata.area.mapper.AreaPropertiesESMapper;
import es.redmic.es.maintenance.area.mapper.AreaClassificationESMapper;
import es.redmic.es.maintenance.area.service.AreaTypeESService;
import es.redmic.es.maintenance.area.service.ThematicTypeESService;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.geojson.area.dto.AreaDTO;
import es.redmic.models.es.geojson.common.model.GeoMultiPolygonData;
import es.redmic.models.es.maintenance.areas.model.ThematicType;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class AreaMapperTest extends MapperTestUtil {

	@Mock
	AreaTypeESService areaTypeESService;

	@Mock
	ThematicTypeESService thematicTypeESService;

	@InjectMocks
	AreaClassificationESMapper classificationESMapper;

	@InjectMocks
	AreaESMapper featureMapper;

	@InjectMocks
	AreaPropertiesESMapper mapper;

	String modelOutPath = "/geodata/area/model/area.json", dtoInPath = "/geodata/area/dto/areaIn.json",
			thematicTypeModel = "/data/maintenance/classification/model/classificationTypeWithColour.json",
			areaTypeModel = "/data/maintenance/model/domain.json";

	@Before
	public void setupTest() throws IOException {

		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);
		factory.addMapper(featureMapper);
		factory.addMapper(classificationESMapper);

		ThematicType thematicTypeExpected = (ThematicType) getBean(thematicTypeModel, ThematicType.class);
		DomainES areaTypeExpected = (DomainES) getBean(areaTypeModel, DomainES.class);

		when(thematicTypeESService.findById(any(String.class))).thenReturn(thematicTypeExpected);
		when(areaTypeESService.findById(any(String.class))).thenReturn(areaTypeExpected);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, AreaDTO.class, GeoMultiPolygonData.class);
	}
}
