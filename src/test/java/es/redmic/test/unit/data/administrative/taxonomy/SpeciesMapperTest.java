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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.redmic.es.administrative.service.DocumentESService;
import es.redmic.es.administrative.taxonomy.mapper.SpeciesESMapper;
import es.redmic.es.administrative.taxonomy.repository.TaxonESRepository;
import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.CanaryProtectionESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.EUProtectionESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.EcologyESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.EndemicityESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.InterestESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.OriginESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.PermanenceESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.SpainProtectionESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.StatusESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.TaxonRankESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.TrophicRegimeESService;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.administrative.taxonomy.dto.SpeciesDTO;
import es.redmic.models.es.administrative.taxonomy.model.Species;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class SpeciesMapperTest extends MapperTestUtil {

	@Mock
	DocumentESService documentService;

	@Mock
	CanaryProtectionESService canaryProtectionService;

	@Mock
	EcologyESService ecologyService;

	@Mock
	EndemicityESService endemicityService;

	@Mock
	EUProtectionESService euProtectionService;

	@Mock
	InterestESService interestService;

	@Mock
	OriginESService originService;

	@Mock
	PermanenceESService permanenceService;

	@Mock
	SpainProtectionESService spainProtectionService;

	@Mock
	TrophicRegimeESService trophicRegimeESService;

	@Mock
	TaxonESRepository baseESRepository;

	@Mock
	TaxonESService baseESService;

	@Mock
	StatusESService statusESService;

	@Mock
	TaxonRankESService taxonRankESService;

	@InjectMocks
	SpeciesESMapper mapper;

	String modelOutPath = "/data/administrative/taxonomy/species/model/species.json",
			dtoInPath = "/data/administrative/taxonomy/species/dto/species.json",
			documentModel = "/data/administrative/document/model/document.json",
			domainModel = "/data/maintenance/model/domain.json",
			taxonModel = "/data/administrative/taxonomy/taxon/model/parent.json";

	@Before
	public void setupTest() throws IOException {

		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);

		DomainES domain = (DomainES) getBean(domainModel, DomainES.class);
		when(canaryProtectionService.findById(anyString())).thenReturn(domain);
		when(ecologyService.findById(anyString())).thenReturn(domain);
		when(endemicityService.findById(anyString())).thenReturn(domain);
		when(euProtectionService.findById(anyString())).thenReturn(domain);
		when(interestService.findById(anyString())).thenReturn(domain);
		when(originService.findById(anyString())).thenReturn(domain);
		when(permanenceService.findById(anyString())).thenReturn(domain);
		when(spainProtectionService.findById(anyString())).thenReturn(domain);
		when(trophicRegimeESService.findById(anyString())).thenReturn(domain);
		when(taxonRankESService.findById(anyString())).thenReturn(domain);
		when(statusESService.findById(anyString())).thenReturn(domain);

		JavaType typeTaxon = jacksonMapper.getTypeFactory().constructParametricType(DataHitWrapper.class, Taxon.class);
		DataHitWrapper parent = (DataHitWrapper<?>) getBean(taxonModel, typeTaxon);
		// when(baseESRepository.findById(anyString())).thenReturn(parent);

		when(baseESService.findById(anyString())).thenReturn((Taxon) parent.get_source());

		Document document = (Document) getBean(documentModel, Document.class);
		when(documentService.findById(anyString())).thenReturn(document);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, SpeciesDTO.class, Species.class);
	}
}