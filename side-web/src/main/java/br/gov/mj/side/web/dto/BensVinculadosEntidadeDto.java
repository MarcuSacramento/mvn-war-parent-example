package br.gov.mj.side.web.dto;

import java.util.ArrayList;
import java.util.List;

import br.gov.mj.side.entidades.entidade.Entidade;

public class BensVinculadosEntidadeDto {

    private Entidade entidade;
    private List<BensVinculadosTotalDto> listaBensVinculadosTotal = new ArrayList<BensVinculadosTotalDto>();
    private List<KitsVinculadosTotalDto> listaKitsVinculadosTotal = new ArrayList<KitsVinculadosTotalDto>();
    
    public List<BensVinculadosTotalDto> getListaBensVinculadosTotal() {
        return listaBensVinculadosTotal;
    }

    public void setListaBensVinculadosTotal(List<BensVinculadosTotalDto> listaBensVinculadosTotal) {
        this.listaBensVinculadosTotal = listaBensVinculadosTotal;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public List<KitsVinculadosTotalDto> getListaKitsVinculadosTotal() {
        return listaKitsVinculadosTotal;
    }

    public void setListaKitsVinculadosTotal(List<KitsVinculadosTotalDto> listaKitsVinculadosTotal) {
        this.listaKitsVinculadosTotal = listaKitsVinculadosTotal;
    }

}
