package br.gov.mj.side.web.dto;

import java.io.Serializable;

public class CaminhoCompletoRelatoriosDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String caminhoCompletoRelatorios;

    public String getCaminhoCompletoRelatorios() {
        return caminhoCompletoRelatorios;
    }

    public CaminhoCompletoRelatoriosDto(String caminhoCompletoRelatorios) {
        super();
        this.caminhoCompletoRelatorios = caminhoCompletoRelatorios;
    }

}
