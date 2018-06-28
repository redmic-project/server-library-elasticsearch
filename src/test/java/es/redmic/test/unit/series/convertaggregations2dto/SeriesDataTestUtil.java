package es.redmic.test.unit.series.convertaggregations2dto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.JavaType;

import es.redmic.models.es.series.common.model.SeriesSearchWrapper;
import es.redmic.models.es.series.objectcollecting.dto.ClassificationsForListDTO;
import es.redmic.models.es.series.objectcollecting.dto.ClassificationsForPieChartDTO;
import es.redmic.models.es.series.objectcollecting.model.ObjectCollectingSeries;
import es.redmic.models.es.series.timeseries.dto.DataHistogramDTO;
import es.redmic.test.unit.geodata.common.JsonToBeanTestUtil;
import es.redmic.test.utils.ConfigMapper;
import es.redmic.test.utils.OrikaScanBeanTest;

public abstract class SeriesDataTestUtil extends JsonToBeanTestUtil {

	protected OrikaScanBeanTest factory = new OrikaScanBeanTest();

	private ConfigMapper configTest;

	public SeriesDataTestUtil(ConfigMapper configTest) throws IOException {
		this.configTest = configTest;
	}

	@Parameters
	public static Collection<ConfigMapper> data() {
		Collection<ConfigMapper> config = new ArrayList<ConfigMapper>();

		config.add(new ConfigMapper().setDataIn("/series/objectcollecting/model/classificationForList.json")
				.setDataOut("/series/objectcollecting/dto/classificationForList.json")
				.setOutClass(ClassificationsForListDTO.class).setInClass(ObjectCollectingSeries.class));

		config.add(new ConfigMapper().setDataIn("/series/objectcollecting/model/classificationForListIncomplete.json")
				.setDataOut("/series/objectcollecting/dto/classificationForListIncomplete.json")
				.setOutClass(ClassificationsForListDTO.class).setInClass(ObjectCollectingSeries.class));

		config.add(new ConfigMapper().setDataIn("/series/objectcollecting/model/classificationForPieChart.json")
				.setDataOut("/series/objectcollecting/dto/classificationForPieChart.json")
				.setOutClass(ClassificationsForPieChartDTO.class).setInClass(ObjectCollectingSeries.class));

		config.add(new ConfigMapper().setDataIn("/series/objectcollecting/model/classificationTotalForLineChart.json")
				.setDataOut("/series/objectcollecting/dto/classificationTotalForLineChart.json")
				.setOutClass(DataHistogramDTO.class).setInClass(ObjectCollectingSeries.class));

		return config;
	}

	@Test
	public void convertAggregations2Dto() throws JSONException, IOException {

		JavaType modelWrapperType = jacksonMapper.getTypeFactory().constructParametricType(SeriesSearchWrapper.class,
				configTest.getInClass());
		SeriesSearchWrapper<ObjectCollectingSeries> result = jacksonMapper
				.readValue(getJsonString(configTest.getDataIn()), modelWrapperType);

		Object dtoOut = factory.getMapperFacade().convert(result.getAggregations(), configTest.getOutClass(), null);
		String dtoStringExpected = getJsonString(configTest.getDataOut());

		JSONAssert.assertEquals(jacksonMapper.writeValueAsString(dtoOut), dtoStringExpected, false);
	}
}
