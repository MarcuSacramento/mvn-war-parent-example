package br.gov.mj.side.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SenhaPadraoUtil {
    private static SenhaPadraoUtil INSTANCE = new SenhaPadraoUtil();
    private static String pattern = null;

    private SenhaPadraoUtil() {
    }

    public static SenhaPadraoUtil buildPadrao(boolean forceSpecialChar, boolean forceCapitalLetter, boolean forceNumber, int minLength, int maxLength) {
        StringBuilder patternBuilder = new StringBuilder("((?=.*[a-z])");

        if (forceSpecialChar) {
            patternBuilder.append("(?=.*[@#$%])");
        }

        if (forceCapitalLetter) {
            patternBuilder.append("(?=.*[A-Z])");
        }

        if (forceNumber) {
            patternBuilder.append("(?=.*\\d)");
        }

        patternBuilder.append(".{" + minLength + "," + maxLength + "})");
        pattern = patternBuilder.toString();

        return INSTANCE;
    }

    public boolean validarSenha(final String password) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(password);
        return m.matches();
    }
}