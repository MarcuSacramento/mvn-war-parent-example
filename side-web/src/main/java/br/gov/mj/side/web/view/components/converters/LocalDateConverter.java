package br.gov.mj.side.web.view.components.converters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

public class LocalDateConverter implements IConverter<LocalDate> {

    private static final long serialVersionUID = 1L;

	@Override
	public LocalDate convertToObject(String value, Locale locale)
			throws ConversionException {
		 return LocalDate.parse(value);
	}

	@Override
	public String convertToString(LocalDate value, Locale locale) {
		DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return sdfPadraoBR.format(value);
	}
}
