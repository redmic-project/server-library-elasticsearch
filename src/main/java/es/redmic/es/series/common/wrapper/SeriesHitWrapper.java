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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import es.redmic.models.es.common.model.BaseAbstractES;
import es.redmic.models.es.common.model.HitWrapper;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SeriesHitWrapper<TModel extends BaseAbstractES> extends HitWrapper {

	private String _parent;

	private String _routing;

	private TModel _source;

	public String get_parent() {
		return _parent;
	}

	public void set_parent(String _parent) {
		this._parent = _parent;
	}

	public String get_routing() {
		return _routing;
	}

	public void set_routing(String _routing) {
		this._routing = _routing;
	}

	public TModel get_source() {
		return _source;
	}

	public void set_source(TModel _source) {
		this._source = _source;
	}

}
