package es.redmic.test.unit.data.es2dto;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.JavaType;

import es.redmic.models.es.administrative.dto.ActivityDTO;
import es.redmic.models.es.administrative.dto.ContactDTO;
import es.redmic.models.es.administrative.dto.DocumentDTO;
import es.redmic.models.es.administrative.dto.OrganisationDTO;
import es.redmic.models.es.administrative.dto.PlatformDTO;
import es.redmic.models.es.administrative.dto.ProgramDTO;
import es.redmic.models.es.administrative.dto.ProjectDTO;
import es.redmic.models.es.administrative.model.Activity;
import es.redmic.models.es.administrative.model.Contact;
import es.redmic.models.es.administrative.model.Document;
import es.redmic.models.es.administrative.model.Organisation;
import es.redmic.models.es.administrative.model.Platform;
import es.redmic.models.es.administrative.model.Program;
import es.redmic.models.es.administrative.model.Project;
import es.redmic.models.es.administrative.taxonomy.dto.AnimalDTO;
import es.redmic.models.es.administrative.taxonomy.dto.KingdomDTO;
import es.redmic.models.es.administrative.taxonomy.dto.SpeciesDTO;
import es.redmic.models.es.administrative.taxonomy.dto.TaxonDTO;
import es.redmic.models.es.administrative.taxonomy.model.Animal;
import es.redmic.models.es.administrative.taxonomy.model.Species;
import es.redmic.models.es.administrative.taxonomy.model.Taxon;
import es.redmic.models.es.atlas.dto.LayerDTO;
import es.redmic.models.es.atlas.model.LayerModel;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.models.es.maintenance.administrative.dto.ActivityFieldDTO;
import es.redmic.models.es.maintenance.administrative.dto.ActivityTypeDTO;
import es.redmic.models.es.maintenance.administrative.dto.CountryDTO;
import es.redmic.models.es.maintenance.administrative.model.ActivityType;
import es.redmic.models.es.maintenance.administrative.model.Country;
import es.redmic.models.es.maintenance.areas.dto.ThematicTypeDTO;
import es.redmic.models.es.maintenance.areas.model.ThematicType;
import es.redmic.models.es.maintenance.device.dto.DeviceDTO;
import es.redmic.models.es.maintenance.device.model.Device;
import es.redmic.models.es.maintenance.line.dto.LineTypeDTO;
import es.redmic.models.es.maintenance.line.model.LineType;
import es.redmic.models.es.maintenance.objects.dto.ObjectTypeDTO;
import es.redmic.models.es.maintenance.objects.model.ObjectType;
import es.redmic.models.es.maintenance.parameter.dto.MetricDefinitionDTO;
import es.redmic.models.es.maintenance.parameter.dto.ParameterDTO;
import es.redmic.models.es.maintenance.parameter.dto.UnitDTO;
import es.redmic.models.es.maintenance.parameter.model.MetricDefinition;
import es.redmic.models.es.maintenance.parameter.model.Parameter;
import es.redmic.models.es.maintenance.parameter.model.Unit;
import es.redmic.models.es.maintenance.point.dto.InfrastructureTypeDTO;
import es.redmic.models.es.maintenance.point.model.InfrastructureType;
import es.redmic.models.es.maintenance.qualifiers.dto.AttributeTypeDTO;
import es.redmic.models.es.maintenance.qualifiers.model.AttributeType;
import es.redmic.test.unit.geodata.common.JsonToBeanTestUtil;
import es.redmic.test.utils.ConfigMapper;
import es.redmic.test.utils.OrikaScanBeanTest;

public abstract class DataTestUtil extends JsonToBeanTestUtil {

	protected OrikaScanBeanTest factory = new OrikaScanBeanTest();

	private ConfigMapper configTest;

	public DataTestUtil(ConfigMapper configTest) throws IOException {
		this.configTest = configTest;
	}

	@Parameters
	public static Collection<ConfigMapper> data() {
		Collection<ConfigMapper> config = new ArrayList<ConfigMapper>();

		config.add(new ConfigMapper().setDataIn("/data/maintenance/model/searchWrapperDomainESModel.json")
				.setDataOut("/data/maintenance/dto/searchWrapperDomainESDTO.json").setInClass(DomainES.class)
				.setOutClass(ActivityFieldDTO.class));

		config.add(
				new ConfigMapper().setDataIn("/data/maintenance/activitytype/model/searchWrapperActivityTypeModel.json")
						.setDataOut("/data/maintenance/activitytype/dto/searchWrapperActivityTypeDTO.json")
						.setInClass(ActivityType.class).setOutClass(ActivityTypeDTO.class));

		config.add(new ConfigMapper().setDataIn("/data/maintenance/country/model/searchWrapperCountryModel.json")
				.setDataOut("/data/maintenance/country/dto/searchWrapperCountryDTO.json").setInClass(Country.class)
				.setOutClass(CountryDTO.class));

		config.add(new ConfigMapper()
				.setDataIn("/data/administrative/organisation/model/searchWrapperOrganisationModel.json")
				.setDataOut("/data/administrative/organisation/dto/searchWrapperOrganisationDTO.json")
				.setInClass(Organisation.class).setOutClass(OrganisationDTO.class));

		config.add(new ConfigMapper().setDataIn("/data/administrative/contact/model/searchWrapperContactModel.json")
				.setDataOut("/data/administrative/contact/dto/searchWrapperContactDTO.json").setInClass(Contact.class)
				.setOutClass(ContactDTO.class));

		config.add(new ConfigMapper().setDataIn("/data/administrative/document/model/searchWrapperDocumentModel.json")
				.setDataOut("/data/administrative/document/dto/searchWrapperDocumentDTO.json")
				.setInClass(Document.class).setOutClass(DocumentDTO.class));

		config.add(new ConfigMapper().setDataIn("/data/administrative/device/model/searchWrapperDeviceModel.json")
				.setDataOut("/data/administrative/device/dto/searchWrapperDeviceDTO.json").setInClass(Device.class)
				.setOutClass(DeviceDTO.class));

		config.add(new ConfigMapper().setDataIn("/data/administrative/platform/model/searchWrapperPlatformModel.json")
				.setDataOut("/data/administrative/platform/dto/searchWrapperPlatformDTO.json")
				.setInClass(Platform.class).setOutClass(PlatformDTO.class));

		config.add(new ConfigMapper().setDataIn("/data/maintenance/unit/model/searchWrapperUnitModel.json")
				.setDataOut("/data/maintenance/unit/dto/searchWrapperUnitDTO.json").setInClass(Unit.class)
				.setOutClass(UnitDTO.class));

		config.add(new ConfigMapper().setDataIn("/data/maintenance/parameter/model/searchWrapperParameterModel.json")
				.setDataOut("/data/maintenance/parameter/dto/searchWrapperParameterDTO.json")
				.setInClass(Parameter.class).setOutClass(ParameterDTO.class));

		config.add(new ConfigMapper()
				.setDataIn("/data/maintenance/metricdefinition/model/searchWrapperMetricDefinitionModel.json")
				.setDataOut("/data/maintenance/metricdefinition/dto/searchWrapperMetricDefinitionDTO.json")
				.setInClass(MetricDefinition.class).setOutClass(MetricDefinitionDTO.class));

		config.add(new ConfigMapper().setDataIn("/data/administrative/activity/model/searchWrapperActivityModel.json")
				.setDataOut("/data/administrative/activity/dto/searchWrapperActivityDTO.json")
				.setInClass(Activity.class).setOutClass(ActivityDTO.class));

		config.add(new ConfigMapper().setDataIn("/data/administrative/project/model/searchWrapperProjectModel.json")
				.setDataOut("/data/administrative/project/dto/searchWrapperProjectDTO.json").setInClass(Project.class)
				.setOutClass(ProjectDTO.class));

		config.add(new ConfigMapper().setDataIn("/data/administrative/program/model/searchWrapperProgramModel.json")
				.setDataOut("/data/administrative/program/dto/searchWrapperProgramDTO.json").setInClass(Program.class)
				.setOutClass(ProgramDTO.class));

		config.add(
				new ConfigMapper().setDataIn("/data/administrative/taxonomy/taxon/model/searchWrapperTaxonModel.json")
						.setDataOut("/data/administrative/taxonomy/taxon/dto/searchWrapperTaxonDTO.json")
						.setInClass(Taxon.class).setOutClass(TaxonDTO.class));

		config.add(
				new ConfigMapper().setDataIn("/data/administrative/taxonomy/taxon/model/searchWrapperKingdomModel.json")
						.setDataOut("/data/administrative/taxonomy/taxon/dto/searchWrapperKingdomDTO.json")
						.setInClass(Taxon.class).setOutClass(KingdomDTO.class));

		config.add(new ConfigMapper()
				.setDataIn("/data/administrative/taxonomy/species/model/searchWrapperSpeciesModel.json")
				.setDataOut("/data/administrative/taxonomy/species/dto/searchWrapperSpeciesDTO.json")
				.setInClass(Species.class).setOutClass(SpeciesDTO.class));

		config.add(
				new ConfigMapper().setDataIn("/data/administrative/taxonomy/animal/model/searchWrapperAnimalModel.json")
						.setDataOut("/data/administrative/taxonomy/animal/dto/searchWrapperAnimalDTO.json")
						.setInClass(Animal.class).setOutClass(AnimalDTO.class));

		// Clasificaciones

		config.add(new ConfigMapper()
				.setDataIn("/data/maintenance/classification/model/searchWrapperClassificationTypeModel.json")
				.setDataOut("/data/maintenance/classification/dto/searchWrapperClassificationTypeDTO.json")
				.setInClass(ObjectType.class).setOutClass(ObjectTypeDTO.class));

		config.add(new ConfigMapper()
				.setDataIn("/data/maintenance/classification/model/searchWrapperClassificationTypeModel.json")
				.setDataOut("/data/maintenance/classification/dto/searchWrapperClassificationTypeDTO.json")
				.setInClass(AttributeType.class).setOutClass(AttributeTypeDTO.class));

		config.add(new ConfigMapper()
				.setDataIn("/data/maintenance/classification/model/searchWrapperClassificationTypeWithRemarkModel.json")
				.setDataOut("/data/maintenance/classification/dto/searchWrapperClassificationTypeWithRemarkDTO.json")
				.setInClass(LineType.class).setOutClass(LineTypeDTO.class));

		config.add(new ConfigMapper()
				.setDataIn("/data/maintenance/classification/model/searchWrapperClassificationTypeWithRemarkModel.json")
				.setDataOut("/data/maintenance/classification/dto/searchWrapperClassificationTypeWithRemarkDTO.json")
				.setInClass(InfrastructureType.class).setOutClass(InfrastructureTypeDTO.class));

		config.add(new ConfigMapper()
				.setDataIn("/data/maintenance/classification/model/searchWrapperClassificationTypeWithColourModel.json")
				.setDataOut("/data/maintenance/classification/dto/searchWrapperClassificationTypeWithColourDTO.json")
				.setInClass(ThematicType.class).setOutClass(ThematicTypeDTO.class));

		//

		config.add(new ConfigMapper().setDataIn("/data/atlas/layer/model/searchWrapperLayerModel.json")
				.setDataOut("/data/atlas/layer/dto/searchWrapperLayerDTO.json").setInClass(LayerModel.class)
				.setOutClass(LayerDTO.class));

		return config;
	}

	@Test
	public void test() throws IOException, ClassNotFoundException, JSONException {

		JavaType type = jacksonMapper.getTypeFactory().constructParametricType(DataSearchWrapper.class,
				configTest.getInClass());
		DataSearchWrapper<?> beanIn = (DataSearchWrapper<?>) getBean(configTest.getDataIn(), type);
		String expected = getJsonString(configTest.getDataOut());
		Object beanOut = factory.getMapperFacade().map(beanIn.getHits(), JSONCollectionDTO.class,
				getContext(configTest.getOutClass()));
		String result = jacksonMapper.writeValueAsString(beanOut);

		JSONAssert.assertEquals(result, expected, true);
	}
}
