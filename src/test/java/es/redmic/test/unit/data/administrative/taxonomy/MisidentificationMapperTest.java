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

import es.redmic.es.administrative.service.DocumentESService;
import es.redmic.es.administrative.taxonomy.mapper.MisidentificationESMapper;
import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.geodata.citation.service.CitationESService;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.administrative.taxonomy.dto.MisidentificationDTO;
import es.redmic.models.es.administrative.taxonomy.model.Misidentification;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.test.unit.geodata.common.MapperTestUtil;
import ma.glasnost.orika.metadata.TypeFactory;

@RunWith(MockitoJUnitRunner.class)
public class MisidentificationMapperTest extends MapperTestUtil {

	@Mock
	DocumentESService documentService;

	@Mock
	TaxonESService taxonESService;

	@Mock
	CitationESService citationService;

	@InjectMocks
	MisidentificationESMapper mapper;

	String modelOutPath = "/data/administrative/taxonomy/misidentification/model/misidentification.json",
			dtoInPath = "/data/administrative/taxonomy/misidentification/dto/misidentification.json",
			documentModel = "/data/administrative/document/model/document.json",
			taxonModel = "/data/administrative/taxonomy/taxon/model/taxon.json",
			citationDTOPath = "/data/administrative/taxonomy/misidentification/dto/citation.json";

	@Before
	public void setupTest() throws IOException {

		factory.addObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));
		factory.addMapper(mapper);

		Taxon taxon = (Taxon) getBean(taxonModel, Taxon.class);
		when(taxonESService.findById(anyString())).thenReturn(taxon);

		Document document = (Document) getBean(documentModel, Document.class);
		when(documentService.findById(anyString())).thenReturn(document);

		// CitationDTO citationDTO = (CitationDTO) getBean(citationDTOPath,
		// CitationDTO.class);
		// when(citationService.searchById(anyString())).thenReturn(citationDTO);
	}

	@Test
	public void mapperDtoToModel() throws JsonParseException, JsonMappingException, IOException, JSONException {

		mapperDtoToModel(dtoInPath, modelOutPath, MisidentificationDTO.class, Misidentification.class);
	}
}