package br.gov.mj.side.web.dto;

import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;

public class BensVinculadosLocaisEntregaDto {

    private LocalEntregaEntidade localEntrega;
    private Integer quantidade;
    
    public LocalEntregaEntidade getLocalEntrega() {
        return localEntrega;
    }
    public void setLocalEntrega(LocalEntregaEntidade localEntrega) {
        this.localEntrega = localEntrega;
    }
    public Integer getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

}
