package br.gov.mj.side.web.view.components.converters;

import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import br.gov.mj.side.web.util.MascaraUtils;

public class RemoveMascarasStringConverter implements IConverter<String> {

    private static final long serialVersionUID = 1L;
   
    @Override
    public String convertToObject(String value, Locale locale) throws ConversionException {
        return MascaraUtils.limparFormatacaoMascara(value);
    }

    @Override
    public String convertToString(String value, Locale locale) {
        return MascaraUtils.formatarMascaraCpfCnpj(value);
    }

}
