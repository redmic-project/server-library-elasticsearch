package es.redmic.test.unit.postupdate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import es.redmic.es.administrative.repository.ActivityESRepository;
import es.redmic.es.administrative.repository.DocumentESRepository;
import es.redmic.es.administrative.repository.OrganisationESRepository;
import es.redmic.es.administrative.repository.PlatformESRepository;
import es.redmic.es.administrative.repository.ProgramESRepository;
import es.redmic.es.administrative.repository.ProjectESRepository;
import es.redmic.es.administrative.service.ActivityESService;
import es.redmic.es.administrative.service.DocumentESService;
import es.redmic.es.administrative.service.OrganisationESService;
import es.redmic.es.administrative.service.PlatformESService;
import es.redmic.es.administrative.service.ProgramESService;
import es.redmic.es.administrative.service.ProjectESService;
import es.redmic.es.administrative.taxonomy.repository.AnimalESRepository;
import es.redmic.es.administrative.taxonomy.repository.TaxonESRepository;
import es.redmic.es.administrative.taxonomy.service.AnimalESService;
import es.redmic.es.administrative.taxonomy.service.TaxonESService;
import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.es.maintenance.animal.repository.LifeStageESRepository;
import es.redmic.es.maintenance.animal.repository.SexESRepository;
import es.redmic.es.maintenance.animal.service.LifeStageESService;
import es.redmic.es.maintenance.animal.service.SexESService;
import es.redmic.es.maintenance.device.repository.DeviceESRepository;
import es.redmic.es.maintenance.device.repository.DeviceTypeESRepository;
import es.redmic.es.maintenance.device.service.DeviceESService;
import es.redmic.es.maintenance.device.service.DeviceTypeESService;
import es.redmic.es.maintenance.domain.administrative.repository.AccessibilityESRepository;
import es.redmic.es.maintenance.domain.administrative.repository.ActivityFieldESRepository;
import es.redmic.es.maintenance.domain.administrative.repository.ActivityTypeESRepository;
import es.redmic.es.maintenance.domain.administrative.repository.CountryESRepository;
import es.redmic.es.maintenance.domain.administrative.repository.DocumentTypeESRepository;
import es.redmic.es.maintenance.domain.administrative.repository.OrganisationTypeESRepository;
import es.redmic.es.maintenance.domain.administrative.repository.PlatformTypeESRepository;
import es.redmic.es.maintenance.domain.administrative.repository.ProjectGroupESRepository;
import es.redmic.es.maintenance.domain.administrative.repository.ScopeESRepository;
import es.redmic.es.maintenance.domain.administrative.service.AccessibilityESService;
import es.redmic.es.maintenance.domain.administrative.service.ActivityFieldESService;
import es.redmic.es.maintenance.domain.administrative.service.ActivityTypeESService;
import es.redmic.es.maintenance.domain.administrative.service.CountryESService;
import es.redmic.es.maintenance.domain.administrative.service.DocumentTypeESService;
import es.redmic.es.maintenance.domain.administrative.service.OrganisationTypeESService;
import es.redmic.es.maintenance.domain.administrative.service.PlatformTypeESService;
import es.redmic.es.maintenance.domain.administrative.service.ProjectGroupESService;
import es.redmic.es.maintenance.domain.administrative.service.ScopeESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.StatusESRepository;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.TaxonRankESRepository;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.StatusESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.TaxonRankESService;
import es.redmic.es.maintenance.parameter.repository.ParameterESRepository;
import es.redmic.es.maintenance.parameter.repository.ParameterTypeESRepository;
import es.redmic.es.maintenance.parameter.repository.UnitESRepository;
import es.redmic.es.maintenance.parameter.repository.UnitTypeESRepository;
import es.redmic.es.maintenance.parameter.service.ParameterESService;
import es.redmic.es.maintenance.parameter.service.ParameterTypeESService;
import es.redmic.es.maintenance.parameter.service.UnitESService;
import es.redmic.es.maintenance.parameter.service.UnitTypeESService;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.maintenance.administrative.model.ActivityType;
import es.redmic.models.es.maintenance.administrative.model.ActivityTypeCompact;
import es.redmic.models.es.maintenance.administrative.model.Country;
import es.redmic.test.unit.postupdate.common.ReferencesBaseTest;
import es.redmic.test.unit.postupdate.config.DependenceConfig;
import es.redmic.test.unit.postupdate.config.DomainReferencesConfig;
import es.redmic.test.unit.postupdate.config.MetaDataReferencesConfig;
import es.redmic.test.unit.postupdate.config.ReferencesConfig;

@RunWith(Parameterized.class)
public class ReferencesTest extends ReferencesBaseTest {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	public ReferencesTest(ReferencesConfig<?> info) {
		super(info);
	}

	@Parameters
	public static Collection<ReferencesConfig<?>> config() {

		Collection<ReferencesConfig<?>> config = new ArrayList<ReferencesConfig<?>>();
		// @formatter:off
		/*Domains*/
		
		config.add(new DomainReferencesConfig()
			.setServiceClass(AccessibilityESService.class)
			.setRepositoryClass(AccessibilityESRepository.class)
			.addDependence("programESService", ProgramESService.class, ProgramESRepository.class, DomainES.class, "accessibility.id")
			.addDependence("projectESService", ProjectESService.class, ProjectESRepository.class, DomainES.class, "accessibility.id")
			.addDependence("activityESService", ActivityESService.class, ActivityESRepository.class, DomainES.class, "accessibility.id"));
		
		config.add(new DomainReferencesConfig()
			.setServiceClass(ActivityFieldESService.class)
			.setRepositoryClass(ActivityFieldESRepository.class)
			.addDependence("activityTypeESService", ActivityTypeESService.class, ActivityTypeESRepository.class, DomainES.class, "activityField.id"));
		
		config.add(new DomainReferencesConfig()
			.setServiceClass(DeviceTypeESService.class)
			.setRepositoryClass(DeviceTypeESRepository.class)
			.addDependence("deviceESService", DeviceESService.class, DeviceESRepository.class, DomainES.class, "deviceType.id"));
		
		config.add(new DomainReferencesConfig()
			.setServiceClass(DocumentTypeESService.class)
			.setRepositoryClass(DocumentTypeESRepository.class)
			.addDependence("documentESService", DocumentESService.class, DocumentESRepository.class, DomainES.class, "documentType.id"));
		
		config.add(new DomainReferencesConfig()
			.setServiceClass(OrganisationTypeESService.class)
			.setRepositoryClass(OrganisationTypeESRepository.class)
			.addDependence("organisationESService", OrganisationESService.class, OrganisationESRepository.class, DomainES.class, "organisationType.id"));
		
		config.add(new MetaDataReferencesConfig<Country>()
			.setServiceClass(CountryESService.class)
			.setRepositoryClass(CountryESRepository.class)
			.setModelClass(Country.class)
			.addDependence("organisationESService", OrganisationESService.class, OrganisationESRepository.class, Country.class, "country.id")
			.setDataInPath("/data/maintenance/country/model/country.json")
			.setDataInModifiedPath("/data/maintenance/country/model/countryModified.json"));
		
		config.add(new DomainReferencesConfig()
			.setServiceClass(ParameterTypeESService.class)
			.setRepositoryClass(ParameterTypeESRepository.class)
			.addDependence("parameterESService", ParameterESService.class, ParameterESRepository.class, DomainES.class, "parameterType.id"));
		
		config.add(new DomainReferencesConfig()
			.setServiceClass(PlatformTypeESService.class)
			.setRepositoryClass(PlatformTypeESRepository.class)
			.addDependence("platformESService", PlatformESService.class, PlatformESRepository.class, DomainES.class, "platformType.id"));
		
		config.add(new DomainReferencesConfig()
			.setServiceClass(ProjectGroupESService.class)
			.setRepositoryClass(ProjectGroupESRepository.class)
			.addDependence("projectESService", ProjectESService.class, ProjectESRepository.class, DomainES.class, "projectGroup.id"));
		
		config.add(new DomainReferencesConfig()
			.setServiceClass(ScopeESService.class)
			.setRepositoryClass(ScopeESRepository.class)
			.addDependence("programESService", ProgramESService.class, ProgramESRepository.class, DomainES.class, "scope.id")
			.addDependence("projectESService", ProjectESService.class, ProjectESRepository.class, DomainES.class, "scope.id")
			.addDependence("activityESService", ActivityESService.class, ActivityESRepository.class, DomainES.class, "scope.id"));
		
		config.add(new DomainReferencesConfig()
			.setServiceClass(SexESService.class)
			.setRepositoryClass(SexESRepository.class)
			.addDependence("animalESService", AnimalESService.class, AnimalESRepository.class, DomainES.class, "sex.id"));
		
		config.add(new DomainReferencesConfig()
			.setServiceClass(StatusESService.class)
			.setRepositoryClass(StatusESRepository.class)
			.addDependence("taxonESService", TaxonESService.class, TaxonESRepository.class, DomainES.class, "status.id"));
		
		config.add(new DomainReferencesConfig()
			.setServiceClass(TaxonRankESService.class)
			.setRepositoryClass(TaxonRankESRepository.class)
			.addDependence("taxonESService", TaxonESService.class, TaxonESRepository.class, DomainES.class, "rank.id"));
		
		config.add(new DomainReferencesConfig()
			.setServiceClass(UnitTypeESService.class)
			.setRepositoryClass(UnitTypeESRepository.class)
			.addDependence("unitESService", UnitESService.class, UnitESRepository.class, DomainES.class, "unitType.id"));
		
		config.add(new DomainReferencesConfig()
			.setServiceClass(LifeStageESService.class)
			.setRepositoryClass(LifeStageESRepository.class)
			.addDependence("animalESService", AnimalESService.class, AnimalESRepository.class, DomainES.class, "lifeStage.id"));
		
		/*Complex domains and metaData*/
		
		
		
		config.add(new MetaDataReferencesConfig<ActivityType>()
			.setServiceClass(ActivityTypeESService.class)
			.setRepositoryClass(ActivityTypeESRepository.class)
			.setModelClass(ActivityType.class)
			.addDependence("activityESService", ActivityESService.class, ActivityESRepository.class, ActivityTypeCompact.class, "activityType.id")
			.setDataInPath("/data/maintenance/activitytype/model/activityType.json")
			.setDataInModifiedPath("/data/maintenance/activitytype/model/activityTypeModified.json"));
		// @formatter:on
		return config;
	}

	@Override
	@Test
	public void whenChangeReferenceCallPostUpdate() throws Exception {

		super.whenChangeReferenceCallPostUpdate();
	}

	@Override
	protected void verifyInvokeMethod(ReferencesConfig<?> referencesInfo, DependenceConfig dependence) {

		referencesInfo.getReferencesUtil().setClassForMapping(dependence.getModelClass());
		((RWDataESRepository<?>) verify(dependence.getRepository(), times(1)))
				.multipleUpdateByRequest(referencesInfo.getReferencesUtil().getModelToIndex(), dependence.getPath());
	}
}
