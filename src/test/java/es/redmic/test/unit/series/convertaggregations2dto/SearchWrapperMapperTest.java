package es.redmic.test.unit.series.convertaggregations2dto;

import java.io.IOException;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import es.redmic.es.series.common.converter.DataHistogramConverter;
import es.redmic.es.series.common.converter.ItemHistogramConverter;
import es.redmic.es.series.common.converter.ItemStatsHistogramConverter;
import es.redmic.es.series.objectcollecting.converter.ClassificationForListConverter;
import es.redmic.es.series.objectcollecting.converter.ClassificationForPieChartConverter;
import es.redmic.test.utils.ConfigMapper;

@RunWith(Parameterized.class)
public class SearchWrapperMapperTest extends SeriesDataTestUtil {

	public SearchWrapperMapperTest(ConfigMapper configTest) throws IOException {
		super(configTest);

		// @formatter:off
		factory.addConverter(new ClassificationForListConverter());
		
		factory.addConverter(new ClassificationForPieChartConverter());
		
		factory.addConverter(new DataHistogramConverter());
		
		factory.addConverter(new ItemHistogramConverter());
		factory.addConverter(new ItemStatsHistogramConverter());
		// @formatter:on
	}
}
