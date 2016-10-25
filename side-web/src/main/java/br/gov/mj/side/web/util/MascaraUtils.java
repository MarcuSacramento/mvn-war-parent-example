package br.gov.mj.side.web.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.text.MaskFormatter;

/**
 * Classe que fornece métodos para formatar os dados como máscaras
 * 
 * @author william.barreto
 * 
 */
public class MascaraUtils {

    public static String formatarMascaraCep(Object value) {
        MaskFormatter mask;
        try {
            mask = new MaskFormatter("##.###-###");
            mask.setValueContainsLiteralCharacters(false);
            return mask.valueToString(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatarMascaraCpfCnpj(Object value) {
        MaskFormatter mask;
        try {
            if (value == null) {
                return "";
            }
            if (((String) value).length() == 11) {
                mask = new MaskFormatter("###.###.###-###");
            } else {
                mask = new MaskFormatter("##.###.###/####-##");
            }
            mask.setValueContainsLiteralCharacters(false);
            return mask.valueToString(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatarMascaraTelefone(String value) {
        MaskFormatter mask;
        try {
            mask = new MaskFormatter("(##) ####-#####");
            mask.setValueContainsLiteralCharacters(false);
            return mask.valueToString(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatarMascaraDinheiro(BigDecimal value) {
        return NumberFormat.getCurrencyInstance().format(value);
    }

    public static String limparFormatacaoMascara(String valor) {
        return valor.replace(".", "").replace("/", "").replace("-", "").replace("(", "").replace(")", "");
    }
}
