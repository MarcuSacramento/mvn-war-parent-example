package br.gov.mj.side.web.dto;

import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.programa.Programa;

public class FornecedorPesquisaDto extends EntidadePesquisaDto {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Programa programa;
    private Bem bem;

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }
}
