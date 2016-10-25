package br.gov.mj.side.web.util;

import java.text.Normalizer;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

public class UtilDAO {

    private UtilDAO() {
    }

    public static Path<Object> extractObjectOrder(String propertyOrder, Root<?> root) {
        String[] array;
        array = propertyOrder.split("\\.");
        Path<Object> path;
        if (array.length == 0) {
            path = root.get(propertyOrder);
        } else {
            path = root.get(array[0]);
            for (int i = 1; i < array.length; i++) {
                path = path.get(array[i]);
            }
        }
        return path;
    }
    
    public static Path<Object> extractObjectOrder(String propertyOrder, Join<?,?> root) {
        String[] array;
        array = propertyOrder.split("\\.");
        Path<Object> path;
        if (array.length == 0) {
            path = root.get(propertyOrder);
        } else {
            path = root.get(array[0]);
            for (int i = 1; i < array.length; i++) {
                path = path.get(array[i]);
            }
        }
        return path;
    }
    
    
    
    
    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

}
