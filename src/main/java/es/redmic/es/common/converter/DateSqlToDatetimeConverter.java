package es.redmic.es.common.converter;

import java.sql.Date;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

@Component
public class DateSqlToDatetimeConverter extends BidirectionalConverter<java.sql.Date, DateTime> {

	@Override
	public DateTime convertTo(Date source, Type<DateTime> destinationType) {
		return new DateTime(source.getTime());

	}

	@Override
	public Date convertFrom(DateTime source, Type<Date> destinationType) {
		return new Date(source.getMillis());
	}

}
