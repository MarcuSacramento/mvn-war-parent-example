package br.gov.mj.side.web.dto;

import java.io.Serializable;

public class ProgramaComboDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nomePrograma;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomePrograma() {
        return nomePrograma;
    }

    public void setNomePrograma(String nomePrograma) {
        this.nomePrograma = nomePrograma;
    }

    public ProgramaComboDto(Long id, String nomePrograma) {
        super();
        this.id = id;
        this.nomePrograma = nomePrograma;
    }

}
