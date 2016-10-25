package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class MinutaLocaisEntregaItensDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uf;
    private String bem;

    private List<MinutaLocaisEntregaEnderecosDto> listaEnderecos;

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getBem() {
        return bem;
    }

    public void setBem(String bem) {
        this.bem = bem;
    }

    public List<MinutaLocaisEntregaEnderecosDto> getListaEnderecos() {
        return listaEnderecos;
    }

    public void setListaEnderecos(List<MinutaLocaisEntregaEnderecosDto> listaEnderecos) {
        this.listaEnderecos = listaEnderecos;
    }

    public static Comparator<MinutaLocaisEntregaItensDto> getComparator() {
        return new Comparator<MinutaLocaisEntregaItensDto>() {

            @Override
            public int compare(MinutaLocaisEntregaItensDto o1, MinutaLocaisEntregaItensDto o2) {
                int valor = 0;

                valor = o1.getBem().toUpperCase().compareTo(o2.getBem().toUpperCase()) * 1;

                return valor;
            };
        };
    }
}
