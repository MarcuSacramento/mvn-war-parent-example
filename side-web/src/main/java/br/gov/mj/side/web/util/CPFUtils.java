package br.gov.mj.side.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CPFUtils {

    private static final String CPF_MASK = "$1.$2.$3-$4";
    private static final Pattern CPF_PATTERN = Pattern.compile("([0-9]{3})([\\p{Punct}]{3})([\\p{Punct}]{3})([0-9]{2})");

    /**
     * Clean cpf
     * 
     * @param cpf
     * @return
     */
    public static String clean(final String cpf) {
        if (cpf != null) {
            String cpfLimpo = cpf.trim();
            cpfLimpo = cpfLimpo.replaceAll("\\.", "");
            cpfLimpo = cpfLimpo.replaceAll("\\-", "");
            cpfLimpo = cpfLimpo.replaceAll("/", "");
            return cpfLimpo;
        }
        return null;
    }

    /**
     * Returns a formatted <code>String</code> of the given CPF. A fully valid
     * CPF which passed the {@link CPFUtil#validate(String)} test is required.
     * The output matches the format 999.999.999-99.
     * 
     * @param cpf
     *            A full valid CPF.
     * @return Formatted CPF <code>String</code>.
     * @throws IllegalArgumentException
     *             if the given CPF is not valid.
     * @author Jorge Lee
     */
    public static String format(String cpf) {
        if (!validate(cpf)) {
            throw new IllegalArgumentException("Invalid cpf " + cpf);
        }
        StringBuilder builder = new StringBuilder(cpf.replaceAll("[^\\d]", ""));
        builder.insert(3, '.');
        builder.insert(7, '.');
        builder.insert(11, '-');
        return builder.toString();
    }

    /**
     * Generates a random full valid CPF. The purpose of this method is to help
     * testing procedures by generating valid test data.
     * 
     * @return A full valid CPF <code>String</code>.
     * @author Jorge Lee
     */
    public static String generate() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            builder.append(Math.round(Math.random() * 9));
        }
        builder.append(getValidationDigits(builder.toString()));
        return builder.toString();
    }

    /**
     * A formatted version of {@link CPFUtil#generate()}. The output is
     * formatted according to {@link CPFUtil#format(String)}.
     * 
     * @return A formatted and full valid CPF <code>String</code>.
     * @author Jorge Lee
     */
    public static String generateFormatted() {
        return format(generate());
    }

    /**
     * Calculate the validation digits based on the government defined method.
     * 
     * @param cpf
     *            A format valid CPF with size between 9 (without validation
     *            digits) and 11 (with validation digits). Existing validation
     *            digits will be ignored.
     * @return A string with two digits.
     * @throws IllegalArgumentException
     *             if input is null or not valid.
     * @author Jorge Lee
     */
    protected static String getValidationDigits(String cpf) {

        if (cpf == null || !cpf.matches("[\\d]{9,11}")) {
            throw new IllegalArgumentException("CPF is not valid: " + cpf);
        }
        // calculate both digit
        int d1 = 0, d2 = 0;
        for (int i = 0; i < 9; i++) {
            d1 += Integer.parseInt(cpf.substring(i, i + 1)) * (10 - i);
            d2 += Integer.parseInt(cpf.substring(i, i + 1)) * (11 - i);
        }
        d1 = 11 - d1 % 11;
        d1 = (d1 > 9) ? 0 : d1;
        // complete using the previous calculated digit
        d2 += d1 * 2;
        d2 = 11 - d2 % 11;
        d2 = (d2 > 9) ? 0 : d2;
        return "" + d1 + d2;
    }

    /**
     * Total validation of the CPF ignoring the format. All non-numeric
     * characters will be ignored. Validation digits are tested as well.
     * 
     * @param cpf
     *            The CPF to be tested.
     * @return true if CPF is full valid.
     * @author Jorge Lee
     */
    public static boolean validate(String cpf) {
        if (cpf == null) {
            return false;
        }
        if (clean(cpf).equals("00000000000") || clean(cpf).equals("11111111111") || clean(cpf).equals("22222222222") || clean(cpf).equals("33333333333") || clean(cpf).equals("44444444444") || clean(cpf).equals("55555555555") || clean(cpf).equals("66666666666") || clean(cpf).equals("77777777777")
                || clean(cpf).equals("88888888888") || clean(cpf).equals("99999999999")) {
            return false;
        }
        String _cpf = cpf.replaceAll("[^\\d]", "");
        if (!_cpf.matches("[\\d]{11}"))
            return false;
        return _cpf.equals(_cpf.substring(0, 9) + getValidationDigits(_cpf));
    }

    /*
     * Irá mascarar parte do CPF
     * 
     * @param word = a palavra que será mascarada
     * 
     * @param mascara = o simbolo escolhido que irá substituir os caracteres
     * 
     * @param posicao = a partir de que ponto do cpf os caracteres serão
     * mascarados
     */

    public static String mascararCpf(String word, char mascara, int posicao) {
        int i = 0;
        StringBuilder sb = new StringBuilder(word);
        while (i < word.length()) {
            if (i > posicao && i < 12 && sb.charAt(i) != '.' && sb.charAt(i) != '-') {
                sb.setCharAt(i, mascara);
            }
            i++;
        }
        return sb.toString();
    }

    /**
     * Mascara o cpf e esconde caracteres. Ex: para o número 11111111111 será
     * retornado 111.***.***-11
     * 
     * @param CPF
     *            sem mascara
     * @return CPF formatado com caracteres escondidos
     */
    public static String esconderCpfMascarado(String value) {
        String cpfMakedAsterisc = "";
        cpfMakedAsterisc = value.substring(0, 3);
        cpfMakedAsterisc = cpfMakedAsterisc + "******";
        cpfMakedAsterisc = cpfMakedAsterisc + value.subSequence(9, 11);

        value = cpfMakedAsterisc;

        if (value != null) {
            Matcher matcher = CPF_PATTERN.matcher(value);
            if (matcher.find()) {
                return matcher.replaceAll(CPF_MASK);
            }
        }
        return value;
    }

    public String limparCampos(String valor) {
        String value = valor;
        value = value.replace(".", "");
        value = value.replace("/", "");
        value = value.replace("-", "");
        value = value.replace("(", "");
        value = value.replace(")", "");
        return value;
    }

}
