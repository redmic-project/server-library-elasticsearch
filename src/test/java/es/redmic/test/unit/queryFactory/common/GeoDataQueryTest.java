package es.redmic.test.unit.queryFactory.common;

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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Before;

import es.redmic.models.es.common.query.dto.BboxQueryDTO;
import es.redmic.models.es.common.query.dto.DateLimitsDTO;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;
import es.redmic.models.es.common.query.dto.PrecisionQueryDTO;
import es.redmic.models.es.common.query.dto.RangeOperator;
import es.redmic.models.es.common.query.dto.RegexpDTO;
import es.redmic.models.es.common.query.dto.TextQueryDTO;
import es.redmic.models.es.common.query.dto.ValueQueryDTO;
import es.redmic.models.es.common.query.dto.ZRangeDTO;

public abstract class GeoDataQueryTest {

	protected GeoDataQueryDTO geoDataQueryDTO;

	@Before
	public void setupTest() {

		geoDataQueryDTO = new GeoDataQueryDTO();
	}

	protected String getExpectedQuery(String resourcePath) throws IOException {

		return IOUtils.toString(getClass().getResource(resourcePath).openStream(), Charset.forName(StandardCharsets.UTF_8.name()));
	}

	protected void createDateLimitsQuery() {

		DateLimitsDTO dateLimits = new DateLimitsDTO();

		dateLimits.setStartDate(new DateTime("2015-03-17T00:00:00.000Z"));
		dateLimits.setEndDate(new DateTime("2015-04-17T00:00:00.000Z"));
		geoDataQueryDTO.setDateLimits(dateLimits);
	}

	@SuppressWarnings("serial")
	protected void createFlagsQuery() {

		geoDataQueryDTO.setQFlags(new ArrayList<String>() {
			{
				add("1");
			}
		});
		geoDataQueryDTO.setVFlags(new ArrayList<String>() {
			{
				add("U");
			}
		});
	}

	@SuppressWarnings("serial")
	protected void createValueQuery() {

		ValueQueryDTO valueQueryDTO = new ValueQueryDTO();
		valueQueryDTO.setOp(0.0);
		valueQueryDTO.setOperator(RangeOperator.Equal);

		geoDataQueryDTO.setValue(new ArrayList<ValueQueryDTO>() {
			{
				add(valueQueryDTO);
			}
		});
	}

	protected void createValueQueryExtended() {

		createValueQuery();
		// NOT EQUAL
		ValueQueryDTO notEqual = new ValueQueryDTO();
		notEqual.setOp(2.0).setOperator(RangeOperator.NotEqual);
		geoDataQueryDTO.getValue().add(notEqual);
		// GREATER
		ValueQueryDTO greater = new ValueQueryDTO();
		greater.setOp(2.0).setOperator(RangeOperator.Greater);
		geoDataQueryDTO.getValue().add(greater);
		// GREATER OR EQUAL
		ValueQueryDTO greaterOrEqual = new ValueQueryDTO();
		greaterOrEqual.setOp(3.0).setOperator(RangeOperator.GreaterOrEqual);
		geoDataQueryDTO.getValue().add(greaterOrEqual);
		// LESS
		ValueQueryDTO less = new ValueQueryDTO();
		less.setOp(5.0).setOperator(RangeOperator.Less);
		geoDataQueryDTO.getValue().add(less);
		// LESS OR EQUAL
		ValueQueryDTO lessOrEqual = new ValueQueryDTO();
		lessOrEqual.setOp(5.0).setOperator(RangeOperator.LessOrEqual);
		geoDataQueryDTO.getValue().add(lessOrEqual);
	}

	protected void createZRangeQuery() {

		ZRangeDTO zRangeDTO = new ZRangeDTO();

		zRangeDTO.setMin(-5000.0);
		zRangeDTO.setMax(5000.0);

		geoDataQueryDTO.setZ(zRangeDTO);
	}

	protected void createAccessibilityQuery() {

		geoDataQueryDTO.setAccessibilityIds(new ArrayList<Long>() {
			{
				add(1L);
			}
		});
	}

	protected void createBboxQuery() {

		BboxQueryDTO bbox = new BboxQueryDTO();
		bbox.setBottomRightLat(80.0);
		bbox.setBottomRightLon(-170.0);
		bbox.setTopLeftLat(90.0);
		bbox.setTopLeftLon(-180.0);

		geoDataQueryDTO.setBbox(bbox);
	}

	protected void createRegexpQuery() {

		List<RegexpDTO> regexpList = new ArrayList<RegexpDTO>();
		RegexpDTO regexp = new RegexpDTO();
		regexp.setField("path");
		regexp.setExp("root.[0-9]+");
		regexpList.add(regexp);
		geoDataQueryDTO.setRegexp(regexpList);
	}

	protected void createTextQuery() {

		TextQueryDTO text = new TextQueryDTO();
		text.setText("prueba");
		text.setSearchFields(new String[] { "name" });
		geoDataQueryDTO.setText(text);
	}

	protected void createPrecisionQuery() {

		PrecisionQueryDTO precision = new PrecisionQueryDTO();
		precision.setMin(1.0);
		precision.setMax(10.0);
		geoDataQueryDTO.setPrecision(precision);
	}
}
