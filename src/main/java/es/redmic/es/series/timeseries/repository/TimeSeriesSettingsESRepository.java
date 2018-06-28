package es.redmic.es.series.timeseries.repository;

import es.redmic.es.common.repository.SettingsRepository;
import es.redmic.models.es.series.timeseries.dto.TimeSeriesSettingsDTO;
import es.redmic.models.es.series.timeseries.model.TimeSeriesSettings;


//@Repository
public class TimeSeriesSettingsESRepository extends SettingsRepository<TimeSeriesSettings, TimeSeriesSettingsDTO> {

	private static String[] INDEX = { "user" };
	private static String[] TYPE = { "chartTimeSeries" };

	public TimeSeriesSettingsESRepository() {
		super(INDEX, TYPE);
	}
}
