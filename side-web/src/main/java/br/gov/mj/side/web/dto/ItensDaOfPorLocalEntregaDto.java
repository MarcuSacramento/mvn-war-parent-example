package br.gov.mj.side.web.dto;

import java.io.Serializable;

import br.gov.mj.side.entidades.Bem;

public class ItensDaOfPorLocalEntregaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Bem bem;
    private Integer quantidade;

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
