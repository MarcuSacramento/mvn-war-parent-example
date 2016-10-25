package br.gov.mj.side.web.util;

import java.util.InputMismatchException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CnpjUtil {

    private boolean cnpjValido;

    private static final Log LOGGER = LogFactory.getLog(CnpjUtil.class);

    public CnpjUtil(String cnpj) {
        cnpjValido = isCNPJ(cnpj);
    }

    public CnpjUtil() {
    }

    public String limparCnpj(String cnpjOld) {
        return cnpjOld.replace(".", "").replace("/", "").replace("-", "");
    }

    public boolean primeiraValidacao(String cnpj) {
        // considera-se erro CNPJ's formados por uma sequencia de numeros iguais
        if ("00000000000000".equals(cnpj) || "11111111111111".equals(cnpj) || "22222222222222".equals(cnpj) || "33333333333333".equals(cnpj) || "44444444444444".equals(cnpj) || "55555555555555".equals(cnpj) || "66666666666666".equals(cnpj) || "77777777777777".equals(cnpj)
                || "88888888888888".equals(cnpj) || "99999999999999".equals(cnpj) || cnpj.length() != 14) {
            return false;
        }
        return true;
    }

    public boolean isCNPJ(String cnpjz) {

        String cnpj = limparCnpj(cnpjz);

        // considera-se erro CNPJ's formados por uma sequencia de numeros iguais
        if (!primeiraValidacao(cnpj)) {
            return false;
        }

        char dig13, dig14;
        int sm, i, r, num, peso;

        // "try" - protege o código para eventuais erros de conversao de tipo
        // (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 2;
            for (i = 11; i >= 0; i--) {
                // converte o i-ésimo caractere do CNPJ em um número:
                // por exemplo, transforma o caractere '0' no inteiro 0
                // (48 eh a posição de '0' na tabela ASCII)
                num = (cnpj.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig13 = '0';
            else
                dig13 = (char) ((11 - r) + 48);

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 2;
            for (i = 12; i >= 0; i--) {
                num = (cnpj.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig14 = '0';
            else
                dig14 = (char) ((11 - r) + 48);

            // Verifica se os dígitos calculados conferem com os dígitos
            // informados.
            return dig13 == cnpj.charAt(12) && dig14 == cnpj.charAt(13);

        } catch (InputMismatchException erro) {
            LOGGER.error(erro);
            return false;
        }
    }

    public static String imprimeCNPJ(String cnpj) {
        // máscara do CNPJ: 99.999.999.9999-99
        return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8) + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12, 14);
    }

    public boolean isCnpjValido() {
        return cnpjValido;
    }

    public void setCnpjValido(boolean cnpjValido) {
        this.cnpjValido = cnpjValido;
    }

}
