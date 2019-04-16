package es.redmic.es.tools.distributions.species.service;

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

public abstract class Precision {
	
	public enum PrecisionEnum { 
		G100(100.0), G500(500.0), G1000(1000.0), G5000(5000.0);
		private double value;
		private PrecisionEnum(double value) {
			this.value = value;
		}
		public String toString() {
			return String.valueOf((int)this.value);
		}
	};
	
	public static String getGridSize(Double precision) {
		
		if (precision == null)
			return null;
		precision *= 2; // Comparar con el diámetro
		if (precision <= PrecisionEnum.G100.value)
			return PrecisionEnum.G100.toString();
		if (precision <= PrecisionEnum.G500.value)
			return PrecisionEnum.G500.toString();
		if (precision <= PrecisionEnum.G1000.value)
			return PrecisionEnum.G1000.toString();
		if (precision <= PrecisionEnum.G5000.value)
			return PrecisionEnum.G5000.toString();
		return null;
	}
}
