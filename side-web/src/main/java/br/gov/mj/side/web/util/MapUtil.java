package br.gov.mj.side.web.util;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {

    public static Map<?, ?> getNaoUsados(Map<?, ?> todos, Map<?, ?> utilizados) {
        Map<?, ?> mapaBensDoContrato = new HashMap(todos);
        for (Object key : utilizados.keySet()) {
            if (mapaBensDoContrato.containsKey(key))
                mapaBensDoContrato.remove(key);
        }
        return mapaBensDoContrato;
    }
}
