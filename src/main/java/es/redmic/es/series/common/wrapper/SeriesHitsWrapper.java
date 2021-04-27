package es.redmic.es.series.common.wrapper;

/*-
 * #%L
 * ElasticSearch
 * %%
 * Copyright (C) 2019 - 2021 REDMIC Project / Server
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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import es.redmic.models.es.common.model.BaseAbstractES;
import es.redmic.models.es.common.model.HitsWrapper;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SeriesHitsWrapper <TModel extends BaseAbstractES> extends HitsWrapper {
	public SeriesHitsWrapper() {
	}

	public SeriesHitsWrapper(List<SeriesHitWrapper<TModel>> hits) {
		this.hits = hits;
	}

	private List<SeriesHitWrapper<TModel>> hits;

	public List<SeriesHitWrapper<TModel>> getHits() {
		return hits;
	}

	public void setHits(List<SeriesHitWrapper<TModel>> hits) {
		this.hits = hits;
	}

	@JsonIgnore
	public List<TModel> getSourceList() {

		List<TModel> sources = new ArrayList<>();

		int total = getTotal();
		for(int i=0; i<total; i++) {
			if (hits.get(i) != null)
				sources.add(hits.get(i).get_source());
		}
		return sources;
	}

}
