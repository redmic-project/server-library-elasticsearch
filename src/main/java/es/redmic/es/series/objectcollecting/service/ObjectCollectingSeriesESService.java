package es.redmic.es.series.objectcollecting.service;

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
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.es.geodata.geofixedstation.service.GeoFixedObjectCollectingSeriesESService;
import es.redmic.es.series.common.service.RWSeriesESService;
import es.redmic.es.series.objectcollecting.repository.ObjectCollectingSeriesESRepository;
import es.redmic.exception.elasticsearch.ESTermQueryException;
import es.redmic.models.es.common.dto.ElasticSearchDTO;
import es.redmic.models.es.common.model.DomainES;
import es.redmic.models.es.common.model.ReferencesES;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.maintenance.objects.model.ObjectType;
import es.redmic.models.es.series.common.model.SeriesSearchWrapper;
import es.redmic.models.es.series.objectcollecting.dto.ClassificationsForListDTO;
import es.redmic.models.es.series.objectcollecting.dto.ClassificationsForPieChartDTO;
import es.redmic.models.es.series.objectcollecting.dto.ObjectCollectingSeriesDTO;
import es.redmic.models.es.series.objectcollecting.model.ObjectCollectingSeries;
import es.redmic.models.es.series.timeseries.dto.DataHistogramDTO;

@Service
public class ObjectCollectingSeriesESService
		extends RWSeriesESService<ObjectCollectingSeries, ObjectCollectingSeriesDTO> {

	/* Path de elastic para buscar por accessibility */
	private String confidencePropertyPath = "confidence.id";

	/* Path de elastic para buscar por accessibility */
	private String localityConfidencePropertyPath = "localityConfidence.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<DomainES> confidenceClassInReference = DomainES.class;

	/* Script de elastic para modificar objectType en collecting */
	private String objectTypeUpdateScript = "update-objectType-collecting";

	/* Path de elastic para buscar por objectType */
	private String objectTypeNestedPath = "object.classification";

	private String objectTypePropertyPath = "object.classification.objectType.id";

	/* Clase del modelo indexado en la referencia */
	private static Class<ObjectType> objectTypeClassInReference = ObjectType.class;

	int nestingDepth = 2;

	@Autowired
	GeoFixedObjectCollectingSeriesESService geoFixedSeriesESSevice;

	ObjectCollectingSeriesESRepository repository;

	@Autowired
	public ObjectCollectingSeriesESService(ObjectCollectingSeriesESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	@SuppressWarnings("unchecked")
	public ElasticSearchDTO findClassificationList(DataQueryDTO query) {

		SeriesSearchWrapper<ObjectCollectingSeries> response;

		checkTerm(query.getTerms(), "parentId");
		checkTerm(query.getTerms(), "grandparentId");

		String parentId = (String) query.getTerms().get("parentId"),
				grandparentId = (String) query.getTerms().get("grandparentId");
		response = (SeriesSearchWrapper<ObjectCollectingSeries>) repository.find(query, parentId, grandparentId);

		ClassificationsForListDTO dtoOut = orikaMapper.getMapperFacade().convert(response.getAggregations(),
				ClassificationsForListDTO.class, null);

		return new ElasticSearchDTO(dtoOut.getClassification(), dtoOut.getClassification().size());
	}

	@SuppressWarnings("unchecked")
	public ElasticSearchDTO findClassificationStatistics(DataQueryDTO query) {

		SeriesSearchWrapper<ObjectCollectingSeries> response;

		checkTerm(query.getTerms(), "parentId");
		checkTerm(query.getTerms(), "grandparentId");

		String parentId = (String) query.getTerms().get("parentId"),
				grandparentId = (String) query.getTerms().get("grandparentId");
		response = (SeriesSearchWrapper<ObjectCollectingSeries>) repository.find(query, parentId, grandparentId);

		ClassificationsForPieChartDTO dtoOut = orikaMapper.getMapperFacade().convert(response.getAggregations(),
				ClassificationsForPieChartDTO.class, null);

		return new ElasticSearchDTO(dtoOut.getClassification(), dtoOut.getClassification().size());
	}

	private void checkTerm(Map<String, Object> terms, String term) {

		if (terms == null || !terms.containsKey(term))
			throw new ESTermQueryException(term, "null");
	}

	@SuppressWarnings("unchecked")
	public ElasticSearchDTO findTemporalDataStatistics(DataQueryDTO query) {

		SeriesSearchWrapper<ObjectCollectingSeries> response = (SeriesSearchWrapper<ObjectCollectingSeries>) repository
				.find(query);
		DataHistogramDTO dtoOut = orikaMapper.getMapperFacade().convert(response.getAggregations(),
				DataHistogramDTO.class, null);
		dtoOut.setDataDefinitionIds((List<Integer>) query.getTerms().get("dataDefinition"));
		return new ElasticSearchDTO(dtoOut, dtoOut.getData().size());
	}

	@Override
	public ObjectCollectingSeries mapper(ObjectCollectingSeriesDTO dtoToIndex) {
		return orikaMapper.getMapperFacade().map(dtoToIndex, ObjectCollectingSeries.class);
	}

	/**
	 * Función para modificar las referencias de objectType en collecting en caso de
	 * ser necesario.
	 *
	 * @param reference
	 *            clase que encapsula el modelo de objectType antes y después de ser
	 *            modificado.
	 */

	public void updateObjectType(ReferencesES<ObjectType> reference) {

		updateReferenceDoubleNestedByScript(reference, objectTypeClassInReference, objectTypeNestedPath,
				objectTypePropertyPath, objectTypeUpdateScript);
	}

	/**
	 * Función para modificar las referencias de confidence en collecting en caso de
	 * ser necesario.
	 *
	 * @param reference
	 *            clase que encapsula el modelo de confidence antes y después de ser
	 *            modificado.
	 */

	public void updateConfidence(ReferencesES<DomainES> reference) {

		updateReferenceByScript(reference, confidenceClassInReference, confidencePropertyPath, false);

		updateReferenceByScript(reference, confidenceClassInReference, localityConfidencePropertyPath, false);
	}

	@Override
	protected void postUpdate(ReferencesES<ObjectCollectingSeries> reference) {
	}

	@Override
	protected void postSave(Object model) {
	}

	@Override
	protected void preDelete(Object object) {
	}

	@Override
	protected void postDelete(String id) {
	}
}
