package es.redmic.test.unit.series.es2dto;

import java.io.IOException;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import es.redmic.es.series.common.mapper.SeriesCollectionMapper;
import es.redmic.es.series.common.mapper.SeriesItemMapper;
import es.redmic.test.utils.ConfigMapper;

@RunWith(Parameterized.class)
public class SearchWrapperMapperTest extends SeriesDataTestUtil {

	public SearchWrapperMapperTest(ConfigMapper configTest) throws IOException {
		super(configTest);

		// @formatter:off
		factory.addMapper(new SeriesCollectionMapper());
		factory.addMapper(new SeriesItemMapper());
		// @formatter:on
	}
}
