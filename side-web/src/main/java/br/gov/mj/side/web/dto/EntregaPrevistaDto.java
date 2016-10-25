package br.gov.mj.side.web.dto;

import java.io.Serializable;

public class EntregaPrevistaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nomeBem;
    private Integer quantidade;
    private String enderecoCompleto;

    public String getNomeBem() {
        return nomeBem;
    }

    public void setNomeBem(String nomeBem) {
        this.nomeBem = nomeBem;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getEnderecoCompleto() {
        return enderecoCompleto;
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }

}
