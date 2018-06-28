package es.redmic.test.utils;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import es.redmic.es.common.objectFactory.ModelESFactory;
import es.redmic.es.config.OrikaScanBeanESItfc;
import es.redmic.es.geodata.common.objectfactory.GeometryESFactory;
import es.redmic.models.es.common.model.BaseES;
import ma.glasnost.orika.Converter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.Filter;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.ObjectFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

/**
 * A bean mapper designed for Spring suitable for dependency injection.
 * 
 * Provides an implementation of {@link MapperFacade} which can be injected. In
 * addition it is "Spring aware" in that it can autodiscover any implementations
 * of {@link Mapper} or {@link Converter} that are managed beans within it's
 * parent {@link ApplicationContext}.
 * 
 * @author Ken Blair
 */
@Component
public class OrikaScanBeanTest extends ConfigurableMapper implements OrikaScanBeanESItfc {

	public OrikaScanBeanTest() {
		super(false);
		factory = new DefaultMapperFactory.Builder().build();

		addDefaultActions();
	}

	private MapperFactory factory;

	private void addDefaultActions() {

		factory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.DateTime.class));
		factory.registerObjectFactory(new ModelESFactory(), TypeFactory.<BaseES<?>>valueOf(BaseES.class));

		factory.classMap(Point.class, Point.class).customize(new CustomMapper<Point, Point>() {
		}).register();

		factory.registerObjectFactory(new GeometryESFactory<Point>(), TypeFactory.<Point>valueOf(Point.class));

		factory.classMap(LineString.class, LineString.class).customize(new CustomMapper<LineString, LineString>() {
		}).register();

		factory.registerObjectFactory(new GeometryESFactory<LineString>(),
				TypeFactory.<LineString>valueOf(LineString.class));

		factory.classMap(Polygon.class, Polygon.class).customize(new CustomMapper<Polygon, Polygon>() {
		}).register();

		factory.registerObjectFactory(new GeometryESFactory<Polygon>(), TypeFactory.<Polygon>valueOf(Polygon.class));

		factory.classMap(MultiPolygon.class, MultiPolygon.class)
				.customize(new CustomMapper<MultiPolygon, MultiPolygon>() {
				}).register();

		factory.registerObjectFactory(new GeometryESFactory<MultiPolygon>(),
				TypeFactory.<MultiPolygon>valueOf(MultiPolygon.class));

		factory.classMap(MultiLineString.class, MultiLineString.class)
				.customize(new CustomMapper<MultiLineString, MultiLineString>() {
				}).register();

		factory.registerObjectFactory(new GeometryESFactory<MultiLineString>(),
				TypeFactory.<MultiLineString>valueOf(MultiLineString.class));

		factory.getConverterFactory().registerConverter(new PassThroughConverter(DateTime.class));
		factory.getConverterFactory().registerConverter(new PassThroughConverter(Point.class));
		factory.getConverterFactory().registerConverter(new PassThroughConverter(LineString.class));
		factory.getConverterFactory().registerConverter(new PassThroughConverter(Polygon.class));
		factory.getConverterFactory().registerConverter(new PassThroughConverter(MultiPolygon.class));
		factory.getConverterFactory().registerConverter(new PassThroughConverter(MultiLineString.class));
	}

	public <T> void addObjectFactory(ObjectFactory<T> objectFactory, Type<T> targetType) {

		factory.registerObjectFactory(objectFactory, targetType);
	}

	public void addConverter(final Converter<?, ?> converter) {

		factory.getConverterFactory().registerConverter(converter);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addMapper(final CustomMapper<?, ?> mapper) {

		factory.classMap(mapper.getAType(), mapper.getBType()).byDefault().customize((CustomMapper) mapper).register();
	}

	public void addFilter(final Filter<?, ?> filter) {
		factory.registerFilter(filter);
	}

	@Override
	public MapperFacade getMapperFacade() {
		return factory.getMapperFacade();
	}

	@Override
	public void configureFactoryBuilder(final DefaultMapperFactory.Builder factoryBuilder) {
	}

	@Override
	public void configure(final MapperFactory factory) {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
	}

	@Override
	public void addAllSpringBeans() {
	}
}