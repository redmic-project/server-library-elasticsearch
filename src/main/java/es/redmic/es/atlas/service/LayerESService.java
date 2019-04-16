package es.redmic.es.atlas.service;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.atlas.repository.LayerESRepository;
import es.redmic.es.common.service.HierarchicalESService;
import es.redmic.models.es.administrative.model.ActivityReferences;
import es.redmic.models.es.atlas.dto.LayerDTO;
import es.redmic.models.es.atlas.model.LayerModel;
import es.redmic.models.es.atlas.model.ThemeInspire;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

@Service
public class LayerESService extends HierarchicalESService<LayerModel, LayerDTO> {

	LayerESRepository repository;

	/* Clase del modelo indexado en la referencia */
	private static Class<ActivityReferences> activityClassInReference = ActivityReferences.class;
	private String activityPropertyPath = "activities.id";
	
	/* Clase del modelo indexado en la referencia */
	private static Class<ThemeInspire> themeInspireClassInReference = ThemeInspire.class;
	private String themeInspirePropertyPath = "themeInspire.id";

	@Autowired
	public LayerESService(LayerESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	/**
	 * Función para modificar las referencias de activity en su repositorio en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de activity antes y después de
	 *            ser modificado.
	 */

	public void updateActivity(ReferencesES<?> reference) {

		updateReferenceByScript(reference, activityClassInReference, activityPropertyPath, 1);
	}
	
	/**
	 * Función para modificar las referencias de ThemeInspire en su repositorio en
	 * caso de ser necesario.
	 * 
	 * @param reference
	 *            clase que encapsula el modelo de ThemeInspire antes y después de
	 *            ser modificado.
	 */

	public void updateThemeInspire(ReferencesES<?> reference) {

		updateReferenceByScript(reference, themeInspireClassInReference, themeInspirePropertyPath, 1);
	}

	// Se sobreecribe getAllIds para que al seleccionar todo no se tenga en
	// cuenta los items que no sean layers
	@Override
	public List<String> getAllIds(DataQueryDTO queryDTO, String idProperty) {

		Map<String, Object> terms = new HashMap<String, Object>();
		terms.put("urlSource", "NOT_NULL");
		queryDTO.setTerms(terms);
		return super.getAllIds(queryDTO, idProperty);
	}
}
