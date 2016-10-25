package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.time.LocalDate;

import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.Entidade;

public class ContratoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codigoPrograma;
    private String nomePrograma;
    private Bem bem;
    private Uf uf;
    private Entidade fornecedor;
    private String numeroContrato;
    private LocalDate vigencia;
    private boolean pesquisarProgramasComFormatacaoDeItens = false;

    public boolean isPesquisarProgramasComFormatacaoDeItens() {
        return pesquisarProgramasComFormatacaoDeItens;
    }

    public void setPesquisarProgramasComFormatacaoDeItens(boolean pesquisarProgramasComFormatacaoDeItens) {
        this.pesquisarProgramasComFormatacaoDeItens = pesquisarProgramasComFormatacaoDeItens;
    }

    public String getCodigoPrograma() {
        return codigoPrograma;
    }

    public void setCodigoPrograma(String codigoPrograma) {
        this.codigoPrograma = codigoPrograma;
    }

    public String getNomePrograma() {
        return nomePrograma;
    }

    public void setNomePrograma(String nomePrograma) {
        this.nomePrograma = nomePrograma;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public LocalDate getVigencia() {
        return vigencia;
    }

    public void setVigencia(LocalDate vigencia) {
        this.vigencia = vigencia;
    }

    public Uf getUf() {
        return uf;
    }

    public void setUf(Uf uf) {
        this.uf = uf;
    }

    public Entidade getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Entidade fornecedor) {
        this.fornecedor = fornecedor;
    }

}
