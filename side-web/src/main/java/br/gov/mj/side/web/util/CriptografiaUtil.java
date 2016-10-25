package br.gov.mj.side.web.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CriptografiaUtil {

    private CriptografiaUtil() {
    }

    public static String criptografaSenha(String senhaDescriptografada) {

        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
            byte messageDigest[] = algorithm.digest(senhaDescriptografada.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                hexString.append(String.format("%02X", 0xFF & b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static boolean confereSenha(String senhaDescriptografada, String senhaCriptografada) {

        String criptografada = criptografaSenha(senhaDescriptografada);
        if (criptografada != null) {
            return criptografada.equals(senhaCriptografada);
        } else {
            return false;
        }
    }

}
