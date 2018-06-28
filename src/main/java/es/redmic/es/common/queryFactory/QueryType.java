package es.redmic.es.common.queryFactory;

import java.util.EnumMap;

import es.redmic.es.common.queryFactory.common.BaseQueryUtils;
import es.redmic.es.common.queryFactory.data.ActivityQueryUtils;
import es.redmic.es.common.queryFactory.data.ProgramQueryUtils;
import es.redmic.es.common.queryFactory.data.ProjectQueryUtils;
import es.redmic.es.common.queryFactory.geodata.AnimalTrackingQueryUtils;
import es.redmic.es.common.queryFactory.geodata.AreaQueryUtils;
import es.redmic.es.common.queryFactory.geodata.CitationQueryUtils;
import es.redmic.es.common.queryFactory.geodata.DataQueryUtils;
import es.redmic.es.common.queryFactory.geodata.GeoFixedObjectCollectingSeriesQueryUtils;
import es.redmic.es.common.queryFactory.geodata.GeoFixedTimeSeriesQueryUtils;
import es.redmic.es.common.queryFactory.geodata.InfrastructureQueryUtils;
import es.redmic.es.common.queryFactory.geodata.IsolinesQueryUtils;
import es.redmic.es.common.queryFactory.geodata.PlatformTrackingQueryUtils;
import es.redmic.es.common.queryFactory.geodata.TrackingQueryUtils;
import es.redmic.es.common.queryFactory.series.SeriesQueryUtils;
import es.redmic.models.es.common.DataPrefixType;

public enum QueryType {

	// TODO: Cambiar DataPrefixType a Enum y que QueryType contenga ese enum
	// @formatter:off
	CITATION(DataPrefixType.CITATION),
	ANIMAL_TRACKING(DataPrefixType.ANIMAL_TRACKING),
	PLATFORM_TRACKING(DataPrefixType.PLATFORM_TRACKING),
	TRACKING(DataPrefixType.TRACKING),
	FIXED_TIMESERIES(DataPrefixType.FIXED_TIMESERIES),
	OBJECT_COLLECTING(DataPrefixType.OBJECT_COLLECTING),
	INFRASTRUCTURE(DataPrefixType.INFRASTRUCTURE),
	ISOLINES(DataPrefixType.ISOLINES),
	AREA(DataPrefixType.AREA),
	TOPONYM(DataPrefixType.TOPONYM),
	TIMESERIES(DataPrefixType.TIMESERIES),
	COLLECTINGSERIES(DataPrefixType.COLLECTINGSERIES),
	ACTIVITY(DataPrefixType.ACTIVITY),
	PROJECT(DataPrefixType.PROJECT),
	PROGRAM(DataPrefixType.PROGRAM),
	LAYERS(DataPrefixType.LAYERS),
	OTHER(DataPrefixType.OTHER);
	// @formatter:on

	private String type;

	private QueryType(String type) {
		this.type = type;
	}

	private static final EnumMap<QueryType, Class<? extends BaseQueryUtils>> utils;
	static {
		utils = new EnumMap<>(QueryType.class);
		utils.put(QueryType.OBJECT_COLLECTING, GeoFixedObjectCollectingSeriesQueryUtils.class);
		utils.put(QueryType.FIXED_TIMESERIES, GeoFixedTimeSeriesQueryUtils.class);
		utils.put(QueryType.CITATION, CitationQueryUtils.class);
		utils.put(QueryType.ANIMAL_TRACKING, AnimalTrackingQueryUtils.class);
		utils.put(QueryType.PLATFORM_TRACKING, PlatformTrackingQueryUtils.class);
		utils.put(QueryType.TRACKING, TrackingQueryUtils.class);
		utils.put(QueryType.INFRASTRUCTURE, InfrastructureQueryUtils.class);
		utils.put(QueryType.ISOLINES, IsolinesQueryUtils.class);
		utils.put(QueryType.AREA, AreaQueryUtils.class);
		utils.put(QueryType.TOPONYM, DataQueryUtils.class);
		utils.put(QueryType.TIMESERIES, SeriesQueryUtils.class);
		utils.put(QueryType.COLLECTINGSERIES, SeriesQueryUtils.class);
		utils.put(QueryType.ACTIVITY, ActivityQueryUtils.class);
		utils.put(QueryType.PROJECT, ProjectQueryUtils.class);
		utils.put(QueryType.PROGRAM, ProgramQueryUtils.class);
		utils.put(QueryType.LAYERS, DataQueryUtils.class);
		utils.put(QueryType.OTHER, BaseQueryUtils.class);
	}

	public static Class<? extends BaseQueryUtils> getUtils(String dataType) {

		if (dataType != null) {
			for (QueryType item : QueryType.values()) {
				if (dataType.equalsIgnoreCase(item.type)) {
					return utils.get(item);
				}
			}
		}
		return BaseQueryUtils.class;
	}
}
