package es.redmic.test.unit.postupdate.common;

import java.util.List;

import org.mockito.Mock;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.reflect.Whitebox;

import es.redmic.es.common.repository.DataSessionRepository;
import es.redmic.test.unit.postupdate.config.DependenceConfig;
import es.redmic.test.unit.postupdate.config.ReferencesConfig;
import es.redmic.test.utils.OrikaScanBeanTest;

public abstract class ReferencesBaseTest {
	
	@Mock
	DataSessionRepository<?> dataSessionRepository;
	
	private ReferencesConfig<?> referencesInfo;

	public ReferencesBaseTest(ReferencesConfig<?> info) {
		this.referencesInfo = info;
	}

	protected OrikaScanBeanTest factory = new OrikaScanBeanTest();
	
	public void whenChangeReferenceCallPostUpdate() throws Exception {
		
		Object service = referencesInfo.getServiceClass().getConstructor(referencesInfo.getRepositoryClass()).newInstance(referencesInfo.getRepositoryClass().newInstance());
		
		List<DependenceConfig> dependences = referencesInfo.getDependences();
		injectDependences(dependences, service);
			
		Whitebox.invokeMethod(service, "postUpdate", referencesInfo.getReferencesUtil());
		
		for (int i=0; i<dependences.size(); i++) {
			verifyInvokeMethod(referencesInfo, dependences.get(i));
		}
		
		//assertEquals(referencesInfo.getReferencesUtil().getDifferences().size(), 1);
	}
	
	protected void injectDependences(List<DependenceConfig> dependences, Object service) throws IllegalArgumentException, IllegalAccessException {
		
		for (int i=0; i<dependences.size(); i++) {
			MemberModifier.field(referencesInfo.getServiceClass() , dependences.get(i).getServiceName()).set(service, dependences.get(i).getService());
			
			MemberModifier.field(dependences.get(i).getService().getClass() , "dataSessionRepository").set(dependences.get(i).getService(), dataSessionRepository);
		}
	}
	
	protected abstract void verifyInvokeMethod(ReferencesConfig<?> referencesInfo, DependenceConfig dependence);
}
