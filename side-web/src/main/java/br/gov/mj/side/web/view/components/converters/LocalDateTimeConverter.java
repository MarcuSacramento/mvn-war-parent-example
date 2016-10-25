package br.gov.mj.side.web.view.components.converters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

public class LocalDateTimeConverter implements IConverter<LocalDateTime> {

    private static final long serialVersionUID = 1L;

    @Override
    public LocalDateTime convertToObject(String value, Locale locale) throws ConversionException {
        return LocalDateTime.parse(value);
    }

    @Override
    public String convertToString(LocalDateTime value, Locale locale) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return sdfPadraoBR.format(value);
    }
}
