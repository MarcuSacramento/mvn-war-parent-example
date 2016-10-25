package br.gov.mj.side.web.dto;

import java.io.Serializable;

public class PatrimoniamentoTipo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nomePatrimonio;
    private String numeroPatrimonio;

    public String getNomePatrimonio() {
        return nomePatrimonio;
    }

    public void setNomePatrimonio(String nomePatrimonio) {
        this.nomePatrimonio = nomePatrimonio;
    }

    public String getNumeroPatrimonio() {
        return numeroPatrimonio;
    }

    public void setNumeroPatrimonio(String numeroPatrimonio) {
        this.numeroPatrimonio = numeroPatrimonio;
    }
}
