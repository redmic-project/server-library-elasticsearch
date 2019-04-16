package es.redmic.test.unit.postupdate.common;

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
