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

import es.redmic.es.administrative.taxonomy.mapper.KingdomESMapper;
import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.StatusESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.TaxonRankESService;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class KingdomMapperTest extends MapperTestUtil {

	@Mock
	TaxonESService baseESService;

	@Mock
	StatusESService statusESService;

	@Mock
	TaxonRankESService taxonRankESService;

	@InjectMocks
	KingdomESMapper mapper;

	String modelOutPath = "/data/administrative/taxonomy/taxon/model/kingdom.json",
			dtoInPath = "/data/administrative/taxonomy/taxon/dto/kingdom.json",
			domainModel = "/data/maintenance/model/domain.json";

	@Before
	public void setupTest() throws IOException {

		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);

		DomainES domain = (DomainES) getBean(domainModel, DomainES.class);
		when(taxonRankESService.findById(anyString())).thenReturn(domain);
		when(statusESService.findById(anyString())).thenReturn(domain);

	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, TaxonDTO.class, Taxon.class);
	}
}