package es.redmic.es.data.common.service;

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

import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.ReferencesES;

public interface IRWDataESService<TModel extends BaseES<?>> {

	public TModel save(TModel modelToIndex);
	public TModel update(TModel modelToIndex);
	public void delete(String id);
	
	public void updateReference(ReferencesES<?> reference, Class<?> classInReference, String propertyPath);
	public void updateReferenceByScript(ReferencesES<?> reference, Class<?> classInReference, String propertyPath);
	public void updateReferenceByScript(ReferencesES<?> reference, Class<?> classInReference, String propertyPath, int nestingDepth);
	public void updateReferenceByScript(ReferencesES<?> reference, Class<?> classInReference,
			String propertyPath, Boolean nestedProperty);
	public void updateReferenceByScript(ReferencesES<?> reference, Class<?> classInReference,
			String propertyPath, int nestingDepth, Boolean nestedProperty);
	public void deleteReferenceByScript(String id, String propertyPath, String deleteReferencesScript, Boolean isNestedProperty);
}
