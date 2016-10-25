package br.gov.mj.side.web.dto;

import java.io.Serializable;

import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;

public class PropostaPesquisaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Programa programa;
    private String codigoIdentificadorProgramaPublicado;
    private Entidade entidade;
    private InscricaoPrograma inscricao = new InscricaoPrograma();

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public String getCodigoIdentificadorProgramaPublicado() {
        return codigoIdentificadorProgramaPublicado;
    }

    public void setCodigoIdentificadorProgramaPublicado(String codigoIdentificadorProgramaPublicado) {
        this.codigoIdentificadorProgramaPublicado = codigoIdentificadorProgramaPublicado;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public InscricaoPrograma getInscricao() {
        return inscricao;
    }

    public void setInscricao(InscricaoPrograma inscricao) {
        this.inscricao = inscricao;
    }
}
