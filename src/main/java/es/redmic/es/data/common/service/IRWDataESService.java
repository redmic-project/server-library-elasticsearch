package es.redmic.es.data.common.service;

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
