package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.time.LocalDate;

import br.gov.mj.apoio.entidades.Regiao;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.programa.Programa;

public class PesquisaLicitacaoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codigoPrograma;
    private Programa programa;
    private LocalDate prazoExecucao;
    private Bem item;
    private Regiao regiaoPesquisa;
    private Uf UfPesquisa;

    public String getCodigoPrograma() {
        return codigoPrograma;
    }

    public void setCodigoPrograma(String codigoPrograma) {
        this.codigoPrograma = codigoPrograma;
    }

    public LocalDate getPrazoExecucao() {
        return prazoExecucao;
    }

    public void setPrazoExecucao(LocalDate prazoExecucao) {
        this.prazoExecucao = prazoExecucao;
    }

    public Bem getItem() {
        return item;
    }

    public void setItem(Bem item) {
        this.item = item;
    }

    public Uf getUfPesquisa() {
        return UfPesquisa;
    }

    public void setUfPesquisa(Uf ufPesquisa) {
        UfPesquisa = ufPesquisa;
    }

    public Regiao getRegiaoPesquisa() {
        return regiaoPesquisa;
    }

    public void setRegiaoPesquisa(Regiao regiaoPesquisa) {
        this.regiaoPesquisa = regiaoPesquisa;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }
}
