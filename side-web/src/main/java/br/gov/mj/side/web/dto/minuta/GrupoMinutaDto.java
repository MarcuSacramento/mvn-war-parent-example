package br.gov.mj.side.web.dto.minuta;

import java.io.Serializable;
import java.util.List;

public class GrupoMinutaDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private String nomeGrupo;
    private List<ItemMinutaDto> listaItens;

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public List<ItemMinutaDto> getListaItens() {
        return listaItens;
    }

    public void setListaItens(List<ItemMinutaDto> listaItens) {
        this.listaItens = listaItens;
    }
}
