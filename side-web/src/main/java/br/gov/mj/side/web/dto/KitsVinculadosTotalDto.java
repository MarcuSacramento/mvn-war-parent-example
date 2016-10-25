package br.gov.mj.side.web.dto;

import java.util.ArrayList;
import java.util.List;

import br.gov.mj.side.entidades.Kit;

public class KitsVinculadosTotalDto {

    private Kit kit;
    private Integer quantidade;
    private Integer restantes;
    private List<BensVinculadosLocaisEntregaDto> listaLocaisEntrega = new ArrayList<BensVinculadosLocaisEntregaDto>();
    
    public Kit getKit() {
        return kit;
    }
    public void setKit(Kit kit) {
        this.kit = kit;
    }
    public Integer getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
    public Integer getRestantes() {
        return restantes;
    }
    public void setRestantes(Integer restantes) {
        this.restantes = restantes;
    }
    public List<BensVinculadosLocaisEntregaDto> getListaLocaisEntrega() {
        return listaLocaisEntrega;
    }
    public void setListaLocaisEntrega(List<BensVinculadosLocaisEntregaDto> listaLocaisEntrega) {
        this.listaLocaisEntrega = listaLocaisEntrega;
    }
}
