package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class MinutaLocaisEntregaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nomeGrupo;
    private List<MinutaLocaisEntregaItensDto> listaDeItens;

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public List<MinutaLocaisEntregaItensDto> getListaDeItens() {
        return listaDeItens;
    }

    public void setListaDeItens(List<MinutaLocaisEntregaItensDto> listaDeItens) {
        this.listaDeItens = listaDeItens;
    }

    public void ordenarListaDeBens() {
        Collections.sort(listaDeItens, MinutaLocaisEntregaItensDto.getComparator());
    }
}
