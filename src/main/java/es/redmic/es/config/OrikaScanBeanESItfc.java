package es.redmic.es.config;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Component
public interface OrikaScanBeanESItfc {
	
	public void configureFactoryBuilder(final DefaultMapperFactory.Builder factoryBuilder);

	public void configure(final MapperFactory factory);

	public void setApplicationContext(final ApplicationContext applicationContext);

	public void addAllSpringBeans();

	public void addConverter(final Converter<?, ?> converter);

	public void addMapper(final CustomMapper<?, ?> mapper);
	
	public MapperFacade getMapperFacade();
}