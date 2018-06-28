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
import es.redmic.es.administrative.repository.ContactESRepository;
import es.redmic.es.administrative.repository.PlatformESRepository;
import es.redmic.es.administrative.repository.ProgramESRepository;
import es.redmic.es.administrative.repository.ProjectESRepository;
import es.redmic.es.administrative.service.ActivityESService;
import es.redmic.es.administrative.service.ContactESService;
import es.redmic.es.administrative.service.PlatformESService;
import es.redmic.es.administrative.service.ProgramESService;
import es.redmic.es.administrative.service.ProjectESService;
import es.redmic.es.administrative.taxonomy.repository.AnimalESRepository;
import es.redmic.es.administrative.taxonomy.repository.SpeciesESRepository;
import es.redmic.es.administrative.taxonomy.service.AnimalESService;
import es.redmic.es.administrative.taxonomy.service.SpeciesESService;
import es.redmic.es.atlas.repository.LayerESRepository;
import es.redmic.es.atlas.service.LayerESService;
import es.redmic.es.data.common.repository.RWDataESRepository;
import es.redmic.es.geodata.citation.repository.CitationESRepository;
import es.redmic.es.geodata.citation.service.CitationESService;
import es.redmic.es.geodata.common.repository.RWGeoDataESRepository;
import es.redmic.es.geodata.geofixedstation.repository.GeoFixedTimeSeriesESRepository;
import es.redmic.es.geodata.geofixedstation.service.GeoFixedTimeSeriesESService;
import es.redmic.es.geodata.tracking.animal.repository.AnimalTrackingESRepository;
import es.redmic.es.geodata.tracking.animal.service.AnimalTrackingESService;
import es.redmic.es.maintenance.animal.repository.DestinyESRepository;
import es.redmic.es.maintenance.animal.repository.EndingESRepository;
import es.redmic.es.maintenance.animal.service.DestinyESService;
import es.redmic.es.maintenance.animal.service.EndingESService;
import es.redmic.es.maintenance.device.repository.DeviceESRepository;
import es.redmic.es.maintenance.device.service.DeviceESService;
import es.redmic.es.maintenance.domain.administrative.repository.ContactRoleESRepository;
import es.redmic.es.maintenance.domain.administrative.service.ContactRoleESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.CanaryProtectionESRepository;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.ConfidenceESRepository;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.EUProtectionESRepository;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.EcologyESRepository;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.EndemicityESRepository;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.InterestESRepository;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.OriginESRepository;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.PermanenceESRepository;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.SpainProtectionESRepository;
import es.redmic.es.maintenance.domain.administrative.taxonomy.repository.TrophicRegimeESRepository;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.CanaryProtectionESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.ConfidenceESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.EUProtectionESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.EcologyESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.EndemicityESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.InterestESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.OriginESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.PermanenceESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.SpainProtectionESService;
import es.redmic.es.maintenance.domain.administrative.taxonomy.service.TrophicRegimeESService;
import es.redmic.es.series.common.repository.RWSeriesESRepository;
import es.redmic.es.series.objectcollecting.repository.ObjectCollectingSeriesESRepository;
import es.redmic.es.series.objectcollecting.service.ObjectCollectingSeriesESService;
import es.redmic.models.es.administrative.model.Activity;
import es.redmic.models.es.administrative.model.ActivityReferences;
import es.redmic.models.es.administrative.model.Contact;
import es.redmic.models.es.administrative.model.ContactCompact;
import es.redmic.models.es.administrative.taxonomy.model.Animal;
import es.redmic.models.es.administrative.taxonomy.model.AnimalCompact;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.test.unit.postupdate.common.ReferencesBaseTest;
import es.redmic.test.unit.postupdate.config.DependenceConfig;
import es.redmic.test.unit.postupdate.config.DomainReferencesConfig;
import es.redmic.test.unit.postupdate.config.MetaDataReferencesConfig;
import es.redmic.test.unit.postupdate.config.ReferencesConfig;

@RunWith(Parameterized.class)
public class MultiLevelReferencesTest extends ReferencesBaseTest {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	public MultiLevelReferencesTest(ReferencesConfig<?> info) {
		super(info);
	}

	@Parameters
	public static Collection<ReferencesConfig<?>> config() {

		Collection<ReferencesConfig<?>> config = new ArrayList<ReferencesConfig<?>>();
		// @formatter:off

		/* Domains */

		config.add(new DomainReferencesConfig().setServiceClass(CanaryProtectionESService.class)
				.setRepositoryClass(CanaryProtectionESRepository.class).addDependence("speciesESService",
						SpeciesESService.class, SpeciesESRepository.class, DomainES.class,
						"peculiarity.canaryProtection.id"));

		config.add(new DomainReferencesConfig().setServiceClass(ConfidenceESService.class)
				.setRepositoryClass(ConfidenceESRepository.class)
				.addDependence("citationESService", CitationESService.class, CitationESRepository.class, DomainES.class,
						"properties.collect.confidence.id")
				.addDependence("objectCollectingSeriesESService", ObjectCollectingSeriesESService.class,
						ObjectCollectingSeriesESRepository.class, DomainES.class, "confidence.id"));

		config.add(new DomainReferencesConfig().setServiceClass(DestinyESService.class)
				.setRepositoryClass(DestinyESRepository.class).addDependence("animalESService", AnimalESService.class,
						AnimalESRepository.class, DomainES.class, "recoveries.destiny.id", 2, true));

		config.add(new DomainReferencesConfig().setServiceClass(EcologyESService.class)
				.setRepositoryClass(EcologyESRepository.class).addDependence("speciesESService", SpeciesESService.class,
						SpeciesESRepository.class, DomainES.class, "peculiarity.ecology.id"));

		config.add(new DomainReferencesConfig().setServiceClass(EndemicityESService.class)
				.setRepositoryClass(EndemicityESRepository.class).addDependence("speciesESService",
						SpeciesESService.class, SpeciesESRepository.class, DomainES.class,
						"peculiarity.endemicity.id"));

		config.add(new DomainReferencesConfig().setServiceClass(EndingESService.class)
				.setRepositoryClass(EndingESRepository.class).addDependence("animalESService", AnimalESService.class,
						AnimalESRepository.class, DomainES.class, "recoveries.ending.id", 2, true));

		config.add(new DomainReferencesConfig().setServiceClass(EUProtectionESService.class)
				.setRepositoryClass(EUProtectionESRepository.class).addDependence("speciesESService",
						SpeciesESService.class, SpeciesESRepository.class, DomainES.class,
						"peculiarity.euProtection.id"));

		config.add(new DomainReferencesConfig().setServiceClass(InterestESService.class)
				.setRepositoryClass(InterestESRepository.class).addDependence("speciesESService",
						SpeciesESService.class, SpeciesESRepository.class, DomainES.class, "peculiarity.interest.id"));

		config.add(new DomainReferencesConfig().setServiceClass(OriginESService.class)
				.setRepositoryClass(OriginESRepository.class).addDependence("speciesESService", SpeciesESService.class,
						SpeciesESRepository.class, DomainES.class, "peculiarity.origin.id"));

		config.add(new DomainReferencesConfig().setServiceClass(PermanenceESService.class)
				.setRepositoryClass(PermanenceESRepository.class).addDependence("speciesESService",
						SpeciesESService.class, SpeciesESRepository.class, DomainES.class,
						"peculiarity.permanence.id"));

		config.add(new DomainReferencesConfig().setServiceClass(SpainProtectionESService.class)
				.setRepositoryClass(SpainProtectionESRepository.class).addDependence("speciesESService",
						SpeciesESService.class, SpeciesESRepository.class, DomainES.class,
						"peculiarity.spainProtection.id"));

		config.add(new DomainReferencesConfig().setServiceClass(TrophicRegimeESService.class)
				.setRepositoryClass(TrophicRegimeESRepository.class).addDependence("speciesESService",
						SpeciesESService.class, SpeciesESRepository.class, DomainES.class,
						"peculiarity.trophicRegime.id"));

		config.add(new DomainReferencesConfig().setServiceClass(ContactRoleESService.class)
				.setRepositoryClass(ContactRoleESRepository.class)
				.addDependence("platformESService", PlatformESService.class, PlatformESRepository.class, DomainES.class,
						"contacts.role.id", 2, true)
				.addDependence("activityESService", ActivityESService.class, ActivityESRepository.class,
						DomainES.class, new String[] { "contacts.role.id", "platforms.role.id" },
						new Integer[] { 2, 2 }, new Boolean[] { true, true })
				.addDependence("programESService", ProgramESService.class, ProgramESRepository.class,
						DomainES.class, new String[] { "contacts.role.id", "platforms.role.id" },
						new Integer[] { 2, 2 }, new Boolean[] { true, true })
				.addDependence("projectESService", ProjectESService.class, ProjectESRepository.class,
						DomainES.class, new String[] { "contacts.role.id", "platforms.role.id" },
						new Integer[] { 2, 2 }, new Boolean[] { true, true })
				.addDependence("geoFixedTimeSeriesESService", GeoFixedTimeSeriesESService.class,
						GeoFixedTimeSeriesESRepository.class, DomainES.class,
						"properties.measurements.dataDefinition.contactRole.id", 3, true));

		/* Complex domains and metaData nested */

		config.add(new MetaDataReferencesConfig<Activity>().setServiceClass(ActivityESService.class)
				.setRepositoryClass(ActivityESRepository.class).setModelClass(Activity.class)
				.addDependence("layerESService", LayerESService.class, LayerESRepository.class,
						ActivityReferences.class, "activities.id", 1, true)
				.setDataInPath("/data/administrative/activity/model/activity.json")
				.setDataInModifiedPath("/data/administrative/activity/model/activityModified.json"));

		config.add(new MetaDataReferencesConfig<Contact>().setServiceClass(ContactESService.class)
				.setRepositoryClass(ContactESRepository.class).setModelClass(Contact.class)
				.addDependence("platformESService", PlatformESService.class, PlatformESRepository.class,
						ContactCompact.class, "contacts.contact.id", 2, true)
				.addDependence("activityESService", ActivityESService.class, ActivityESRepository.class,
						ContactCompact.class, new String[] { "contacts.contact.id", "platforms.contact.id" },
						new Integer[] { 2, 2 }, new Boolean[] { true, true })
				.addDependence("programESService", ProgramESService.class, ProgramESRepository.class,
						ContactCompact.class, new String[] { "contacts.contact.id", "platforms.contact.id" },
						new Integer[] { 2, 2 }, new Boolean[] { true, true })
				.addDependence("projectESService", ProjectESService.class, ProjectESRepository.class,
						ContactCompact.class, new String[] { "contacts.contact.id", "platforms.contact.id" },
						new Integer[] { 2, 2 }, new Boolean[] { true, true })
				.addDependence("deviceESService", DeviceESService.class, DeviceESRepository.class, ContactCompact.class,
						"calibrations.contact.id", 2, true)
				.addDependence("geoFixedTimeSeriesESService", GeoFixedTimeSeriesESService.class,
						GeoFixedTimeSeriesESRepository.class, ContactCompact.class,
						"properties.measurements.dataDefinition.contact.id", 3, true)
				.setDataInPath("/data/administrative/contact/model/contact.json")
				.setDataInModifiedPath("/data/administrative/contact/model/contactModified.json"));

		config.add(new MetaDataReferencesConfig<Animal>().setServiceClass(AnimalESService.class)
				.setRepositoryClass(AnimalESRepository.class).setModelClass(Animal.class)
				.addDependence("animalTrackingESService", AnimalTrackingESService.class,
						AnimalTrackingESRepository.class, AnimalCompact.class, "properties.collect.animal.id")
				.setDataInPath("/data/taxonomy/model/animal.json")
				.setDataInModifiedPath("/data/taxonomy/model/animalModified.json"));

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

		if (dependence.getRepository() instanceof RWDataESRepository) {
			((RWDataESRepository<?>) verify(dependence.getRepository(), times(1))).multipleUpdateByScript(
					referencesInfo.getReferencesUtil().getModelToIndex(), dependence.getPath(),
					dependence.getNestingDepth(), dependence.getNestedProperty());
		} else if (dependence.getRepository() instanceof RWSeriesESRepository) {
			((RWSeriesESRepository<?>) verify(dependence.getRepository(), times(1))).multipleUpdateByScript(
					referencesInfo.getReferencesUtil().getModelToIndex(), dependence.getPath(),
					dependence.getNestingDepth(), dependence.getNestedProperty());
		} else {
			((RWGeoDataESRepository<?>) verify(dependence.getRepository(), times(1))).multipleUpdateByScript(
					referencesInfo.getReferencesUtil().getModelToIndex(), dependence.getPath(),
					dependence.getNestingDepth(), dependence.getNestedProperty());

		}
	}
}
