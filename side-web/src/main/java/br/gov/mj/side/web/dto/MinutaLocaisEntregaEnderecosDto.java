package br.gov.mj.side.web.dto;

import java.io.Serializable;

public class MinutaLocaisEntregaEnderecosDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer quantidade;
    private String endereco;

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
