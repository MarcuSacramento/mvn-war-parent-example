package br.gov.mj.side.web.view.components.converters;

import java.util.Locale;

import org.apache.wicket.util.convert.converter.BooleanConverter;

/**
 * 
 * Conversor de Boolean para String
 * @author william.barreto
 *
 */

public class SimNaoBooleanConverter extends BooleanConverter{

    private static final long serialVersionUID = 1L;

    @Override
    public String convertToString(Boolean value, Locale locale) {
        if(value){
            return "Sim";
        }
        return "Não";
    }
}
