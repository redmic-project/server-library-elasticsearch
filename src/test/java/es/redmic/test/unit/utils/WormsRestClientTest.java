package es.redmic.test.unit.utils;

/*-
 * #%L
 * ElasticSearch
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import es.redmic.es.administrative.taxonomy.mapper.WormsESMapper;
import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.administrative.taxonomy.service.TaxonServiceGatewayItfc;
import es.redmic.es.administrative.taxonomy.service.WormsToRedmicService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.StatusESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.TaxonRankESService;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.exception.elasticsearch.ESInsertException;
import es.redmic.exception.taxonomy.InvalidAphiaException;
import es.redmic.exception.utils.WormsUpdateException;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.dto.WormsClassificationDTO;
import es.redmic.models.es.administrative.taxonomy.dto.WormsDTO;
import es.redmic.models.es.administrative.taxonomy.dto.WormsListDTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.taxonomy.dto.RankDTO;
import es.redmic.test.unit.geodata.common.JsonToBeanTestUtil;
import es.redmic.test.utils.OrikaScanBeanTest;

@RunWith(MockitoJUnitRunner.class)
public class WormsRestClientTest extends JsonToBeanTestUtil {

	@Mock
	TaxonESService taxonESService;

	@Mock
	TaxonServiceGatewayItfc taxonService;

	@InjectMocks
	WormsToRedmicService service = new WormsToRedmicService();

	@Mock
	StatusESService statusESService;

	@Mock
	TaxonRankESService taxonRankESService;

	@InjectMocks
	WormsESMapper mapper;

	protected OrikaScanBeanTest factory = new OrikaScanBeanTest();

	@Before
	public void setupTest() {

		Whitebox.setInternalState(service, "APHIA_CLASSIFICATION_BY_APHIAID",
				"http://www.marinespecies.org/rest/AphiaClassificationByAphiaID/");

		Whitebox.setInternalState(service, "APHIA_RECORD_BY_APHIAID",
				"http://www.marinespecies.org/rest/AphiaRecordByAphiaID/");

		Whitebox.setInternalState(service, "APHIA_RECORDS_BY_NAME",
				"http://www.marinespecies.org/rest/AphiaRecordsByName/");

		factory.addMapper(mapper);

		Whitebox.setInternalState(service, "orikaMapper", factory);

		Whitebox.setInternalState(service, "ranks", getRanks());

		Whitebox.setInternalState(service, "speciesRankLabel",
				new ArrayList<String>(Arrays.asList("Species", "Subspecies", "Variety")));

		DomainES kingdom = new DomainES();
		kingdom.setName_en("kingdom");
		when(taxonRankESService.findByName_en(anyString())).thenReturn(kingdom);
		when(statusESService.findByName_en(anyString())).thenReturn(new DomainES());
	}

	@Test
	public void getAphiaClassificationByAphiaId_ReturnClassification_IfRequestIsCorrect() throws Exception {

		WormsClassificationDTO result = service.getAphiaClassificationByAphiaId(136470);

		assertNotNull(result);
		assertNotNull(result.getAphia());
		assertNotNull(result.getRank());
		assertNotNull(result.getScientificname());
	}

	@Test
	public void getAphiaRecordByAphiaId_ReturnRecord_IfRequestIsCorrect() throws Exception {

		WormsDTO result = service.getAphiaRecordByAphiaId(136470);

		assertNotNull(result);
		assertNotNull(result.getAphia());
		assertNotNull(result.getRank());
		assertNotNull(result.getScientificname());
		assertNotNull(result.getAuthority());
		assertNotNull(result.getModified());
		assertNotNull(result.getRank());
		assertNotNull(result.getStatus());
		assertNotNull(result.getUrl());
		assertNotNull(result.getValidAphia());
		assertNotNull(result.getValidName());
	}

	@Test
	public void getAphiaRecordsByName_ReturnRecords_IfRequestIsCorrect() throws Exception {

		WormsListDTO result = service.findAphiaRecordsByScientificName("Leptochelia dubia");

		assertNotNull(result);
		assertTrue(result.size() > 0);

		assertNotNull(result.get(0).getAphia());
		assertNotNull(result.get(0).getRank());
		assertNotNull(result.get(0).getScientificname());
	}

	@Test
	public void getParent_ReturnTaxon_IfExistInClassification() throws Exception {

		TaxonDTO wTaxon = new TaxonDTO();
		wTaxon.setAphia(1066);
		wTaxon.setScientificName("Crustacea");

		Mockito.when(taxonESService.findByAphia(Matchers.anyInt())).thenReturn(wTaxon);

		WormsClassificationDTO classification = (WormsClassificationDTO) getBean("/worms/classification.json",
				WormsClassificationDTO.class);

		TaxonDTO parent = Whitebox.invokeMethod(service, "getParent", 1071, "Class", classification, getRanks());

		assertEquals(parent.getAphia().intValue(), 1066);
	}

	@Test(expected = ItemNotFoundException.class)
	public void getParent_ThrowException_IfNotExistInClassification() throws Exception {

		WormsClassificationDTO classification = (WormsClassificationDTO) getBean("/worms/classification.json",
				WormsClassificationDTO.class);

		Whitebox.invokeMethod(service, "getParent", 1071, "Classtttttt", classification, getRanks());
	}

	@Test
	public void getParentRankIndex_ReturnListRankIndex_WhenRankIsInList() throws Exception {

		RankDTO parentRank = Whitebox.invokeMethod(service, "getParentRank", "Subphylum", getRanks());

		// Se espera el id del rank Phylum
		assertEquals(parentRank.getId().longValue(), 3L);
	}

	@Test(expected = ItemNotFoundException.class)
	public void getParentRankIndex_ThrowException_WhenRankNotIsInList() throws Exception {

		Whitebox.invokeMethod(service, "getParentRank", "noRank", getRanks());
	}

	@Test(expected = ESInsertException.class)
	public void wormsToRedmic_ThrowException_WhenTaxonWithAphiaIsInRedmic() throws Exception {

		Mockito.when(taxonESService.findByAphia(Matchers.anyInt())).thenReturn(new TaxonDTO());

		Whitebox.invokeMethod(service, "wormsToRedmic", 1);
	}

	@Test(expected = ESInsertException.class)
	public void wormsToRedmic_ThrowException_WhenTaxonIsInRedmic() throws Exception {

		Mockito.when(taxonESService.findByAphia(Matchers.anyInt())).thenReturn(null);

		Mockito.when(taxonESService.findByScientificNameRankStatusAndParent(Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyInt())).thenReturn(new TaxonDTO());

		Whitebox.invokeMethod(service, "wormsToRedmic", 622230);
	}

	@Test
	public void wormsToRedmic_NoSaveParent_WhenTaxonIsKingdom() throws Exception {

		Mockito.when(taxonESService.findByAphia(Matchers.anyInt())).thenReturn(null);

		Mockito.when(taxonESService.findByScientificNameRankStatusAndParent(Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyInt())).thenReturn(null);

		TaxonDTO taxon = Whitebox.invokeMethod(service, "wormsToRedmic", 2);

		assertNull(taxon.getParent());

		verify(taxonService, times(0)).save(any());
	}

	@Test
	public void wormsToRedmic_ReturnTaxonDTO_WhenRankOriginalTaxonIsInRedmic() throws Exception {

		Mockito.when(taxonESService.findByAphia(Matchers.anyInt())).thenReturn(null);

		Mockito.when(taxonESService.findByScientificNameRankStatusAndParent(Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyInt())).thenReturn(null);

		TaxonDTO taxon = Whitebox.invokeMethod(service, "wormsToRedmic", 148687);

		assertNotNull(taxon);
		// Como order no está en el listado, se espera el aphia de class
		assertEquals(taxon.getAphia().intValue(), 1071);
	}

	@Test
	public void saveTaxonFromWorms_NotSaveTaxon_IfWormsToRedmicReturnTaxonInRedmic() throws Exception {

		Integer aphia = 1089;

		Mockito.when(taxonESService.findByAphia(aphia)).thenReturn(null);
		Mockito.when(taxonESService.findByAphia(1071)).thenReturn(new TaxonDTO());

		Mockito.when(taxonESService.findByScientificNameRankStatusAndParent(Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyInt())).thenReturn(null);

		Whitebox.invokeMethod(service, "saveTaxonFromWorms", aphia);

		verify(taxonService, times(0)).save(any());
	}

	@Test
	public void saveTaxonFromWorms_SaveTaxon_IfWormsToRedmicNotReturnTaxonInRedmic() throws Exception {

		// Worms to redmic no retorna el mismo aphia pero el retornado no está
		// en redmic.
		Integer aphia = 1089;

		Mockito.when(taxonESService.findByAphia(aphia)).thenReturn(null);
		Mockito.when(taxonESService.findByAphia(1071)).thenReturn(null);

		Mockito.when(taxonESService.findByScientificNameRankStatusAndParent(Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyInt())).thenReturn(null);

		Whitebox.invokeMethod(service, "saveTaxonFromWorms", aphia);

		verify(taxonService, times(1)).save(any());
	}

	@Test
	public void processValidAs_NoSetValidAS_WhenTaxonAphiaAndValidAphiaAreEquals() throws Exception {

		Integer aphia = 22;

		TaxonDTO origin = new TaxonDTO();
		origin.setAphia(aphia);

		TaxonDTO taxon = Whitebox.invokeMethod(service, "processValidAs", origin, aphia);

		assertEquals(taxon, origin);
	}

	@Test
	public void processUpdate_ReturnNull_WhenTaxonAphiaIsNull() throws Exception {

		TaxonDTO taxon = Whitebox.invokeMethod(service, "processUpdate", new TaxonDTO());

		assertNull(taxon);
	}

	@Test
	public void processUpdate_ReturnNull_WhenRedmicWormsUpdatedAndWormsUpdatedAreEquals() throws Exception {

		DateTime wormsUpdated = new DateTime("2014-03-17T00:00:00.000Z");

		TaxonDTO origin = new TaxonDTO();
		origin.setWormsUpdated(wormsUpdated);

		WormsDTO worms = new WormsDTO();
		worms.setModified(wormsUpdated);

		// HttpClient client = Mockito.mock(HttpClient.class);

		// Mockito.when(client.get(Matchers.anyString(), any())).thenReturn(worms);

		TaxonDTO taxon = Whitebox.invokeMethod(service, "processUpdate", origin);

		assertNull(taxon);
	}

	@Test(expected = InvalidAphiaException.class)
	public void processUpdate_ThrowException_WhenAphiaNotIsInWorms() throws Exception {

		TaxonDTO origin = new TaxonDTO();
		origin.setAphia(999999999);

		Whitebox.invokeMethod(service, "processUpdate", origin);
	}

	@Test(expected = WormsUpdateException.class)
	public void processUpdate_ThrowException_WhenRankWormsNotIsInRedmic() throws Exception {

		TaxonDTO origin = new TaxonDTO();
		origin.setAphia(155670);

		Whitebox.invokeMethod(service, "processUpdate", origin);
	}

	@Test
	public void processUpdate_CallProcessAncestorsAndValidAS_WhenChecksAreFulfilled() throws Exception {

		TaxonDTO origin = new TaxonDTO();
		origin.setAphia(2);
		origin.setWormsUpdated(new DateTime("2014-03-17T00:00:00.000Z"));

		TaxonDTO taxon = Whitebox.invokeMethod(service, "processUpdate", origin);

		assertEquals(taxon.getAphia(), origin.getAphia());
		verify(taxonService, times(0)).save(any());
	}

	@Test(expected = WormsUpdateException.class)
	public void wormsToRedmic4Update_ThrowException_WhenRankWormsNotIsInRedmic() throws Exception {

		Whitebox.invokeMethod(service, "wormsToRedmic4Update", 155670);
	}

	@Test
	public void wormsToRedmic4Update_ReturnTaxonDTO_WhenAphiaIsValid() throws Exception {

		TaxonDTO taxon = Whitebox.invokeMethod(service, "wormsToRedmic4Update", 2);
		assertEquals(taxon.getAphia().intValue(), 2);
	}

	public List<RankDTO> getRanks() {

		List<RankDTO> listRanks = new ArrayList<RankDTO>();

		RankDTO rankK = new RankDTO();
		rankK.setId(1L);
		rankK.setName("Kingdom");
		rankK.setName_en("Kingdom");
		listRanks.add(rankK);

		RankDTO rankP = new RankDTO();
		rankP.setId(3L);
		rankP.setName("Filum");
		rankP.setName_en("Phylum");
		listRanks.add(rankP);

		RankDTO rankSP = new RankDTO();
		rankSP.setId(5L);
		rankSP.setName("Subfilum");
		rankSP.setName_en("Subphylum");
		listRanks.add(rankSP);

		RankDTO rankC = new RankDTO();
		rankC.setId(6L);
		rankC.setName("Class");
		rankC.setName_en("Class");
		listRanks.add(rankC);
		return listRanks;
	}

}
