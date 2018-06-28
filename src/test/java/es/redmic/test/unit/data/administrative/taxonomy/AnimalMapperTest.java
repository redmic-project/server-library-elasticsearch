package es.redmic.test.unit.data.administrative.taxonomy;

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

import es.redmic.es.administrative.taxonomy.mapper.AnimalESMapper;
import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.maintenance.animal.service.DestinyESService;
import es.redmic.es.maintenance.animal.service.EndingESService;
import es.redmic.es.maintenance.animal.service.LifeStageESService;
import es.redmic.es.maintenance.animal.service.SexESService;
import es.redmic.models.es.administrative.taxonomy.dto.AnimalDTO;
import es.redmic.models.es.administrative.taxonomy.model.Animal;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class AnimalMapperTest extends MapperTestUtil {

	@Mock
	SexESService sexESService;

	@Mock
	LifeStageESService lifeStageESService;

	@Mock
	TaxonESService taxonESService;

	@Mock
	EndingESService endingESService;

	@Mock
	DestinyESService destinyESService;

	@InjectMocks
	AnimalESMapper mapper;

	String modelOutPath = "/data/administrative/taxonomy/animal/model/animal.json",
			dtoInPath = "/data/administrative/taxonomy/animal/dto/animal.json",
			domainModel = "/data/maintenance/model/domain.json",
			taxonModel = "/data/administrative/taxonomy/taxon/model/taxon.json";

	@Before
	public void setupTest() throws IOException {

		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);

		DomainES domain = (DomainES) getBean(domainModel, DomainES.class);
		when(sexESService.findById(anyString())).thenReturn(domain);
		when(lifeStageESService.findById(anyString())).thenReturn(domain);
		// when(endingESService.findById(anyString())).thenReturn(domain);
		// when(destinyESService.findById(anyString())).thenReturn(domain);

		Taxon taxon = (Taxon) getBean(taxonModel, Taxon.class);
		when(taxonESService.findById(anyString())).thenReturn(taxon);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, AnimalDTO.class, Animal.class);
	}
}