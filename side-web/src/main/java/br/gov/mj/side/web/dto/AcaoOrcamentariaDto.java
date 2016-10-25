package br.gov.mj.side.web.dto;

import java.io.Serializable;

public class AcaoOrcamentariaDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String numero;
    private String nome;

    public AcaoOrcamentariaDto() {

    }

    public AcaoOrcamentariaDto(String numero, String nome) {
        this.numero = numero;
        this.nome = nome;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeFormatado() {
        return this.numero + " - " + this.nome;
    }
}
