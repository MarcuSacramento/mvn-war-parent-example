package br.gov.mj.side.web.view.components.converters;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

public class NumeroProcessoSeiConverter implements IConverter<String> {

    private static final long serialVersionUID = 1L;

    private static final String SEI_MASK = "$1.$2/$3-$4";
    private static final Pattern SEI_PATTERN = Pattern.compile("([0-9]{5})([0-9]{6})([0-9]{4})([0-9]{2})");
    
    @Override
    public String convertToObject(String value, Locale locale) throws ConversionException {
        return value.replace(".", "").replace("/", "").replace("-", "");
    }

    @Override
    public String convertToString(String value, Locale locale) {
        if (value != null) {
            Matcher matcher = SEI_PATTERN.matcher(value);
            if (matcher.find()) {
                return matcher.replaceAll(SEI_MASK);
            }
        }
        return value;
    }
}