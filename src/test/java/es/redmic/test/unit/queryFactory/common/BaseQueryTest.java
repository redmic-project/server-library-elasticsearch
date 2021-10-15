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
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.DateLimitsDTO;
import es.redmic.models.es.common.query.dto.RegexpDTO;
import es.redmic.models.es.common.query.dto.TextQueryDTO;

public abstract class BaseQueryTest {

	protected DataQueryDTO dataQueryDTO;

	@Before
	public void setupTest() {

		dataQueryDTO = new DataQueryDTO();
	}

	protected String getExpectedQuery(String resourcePath) throws IOException {

		return IOUtils.toString(getClass().getResource(resourcePath).openStream(), Charset.forName(StandardCharsets.UTF_8.name()));
	}

	protected void createDateLimitsQuery() {

		DateLimitsDTO dateLimits = new DateLimitsDTO();

		dateLimits.setStartDate(new DateTime("2015-03-17T00:00:00.000Z"));
		dateLimits.setEndDate(new DateTime("2015-04-17T00:00:00.000Z"));
		dataQueryDTO.setDateLimits(dateLimits);
	}

	protected void createBboxQuery() {

		BboxQueryDTO bbox = new BboxQueryDTO();
		bbox.setBottomRightLat(80.0);
		bbox.setBottomRightLon(-170.0);
		bbox.setTopLeftLat(90.0);
		bbox.setTopLeftLon(-180.0);

		dataQueryDTO.setBbox(bbox);
	}

	protected void createRegexpQuery() {

		List<RegexpDTO> regexpList = new ArrayList<RegexpDTO>();
		RegexpDTO regexp = new RegexpDTO();
		regexp.setField("path");
		regexp.setExp("root.[0-9]+");
		regexpList.add(regexp);
		dataQueryDTO.setRegexp(regexpList);
	}

	protected void createTextQuery() {

		TextQueryDTO text = new TextQueryDTO();
		text.setText("prueba");
		text.setSearchFields(new String[] { "name" });
		dataQueryDTO.setText(text);
	}

	protected void createAccessibilityQuery() {

		dataQueryDTO.setAccessibilityIds(new ArrayList<Long>() {
			{
				add(1L);
			}
		});
	}
}
