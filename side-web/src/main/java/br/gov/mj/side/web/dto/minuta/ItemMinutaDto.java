package br.gov.mj.side.web.dto.minuta;

import java.io.Serializable;

public class ItemMinutaDto implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String nomeGrupo;
    private String nomeItem;
    private String especificacoes;
    private String unidadeMedida;
    private String quantidadeImediata;
    private String quantidadeRegistrar;
    private String valorUnitario;
    private String valorTotalRegistrar;
    private String valorTotalImediato;
    public String getNomeGrupo() {
        return nomeGrupo;
    }
    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }
    public String getNomeItem() {
        return nomeItem;
    }
    public void setNomeItem(String nomeItem) {
        this.nomeItem = nomeItem;
    }
    public String getEspecificacoes() {
        return especificacoes;
    }
    public void setEspecificacoes(String especificacoes) {
        this.especificacoes = especificacoes;
    }
    public String getUnidadeMedida() {
        return unidadeMedida;
    }
    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }
    public String getQuantidadeImediata() {
        return quantidadeImediata;
    }
    public void setQuantidadeImediata(String quantidadeImediata) {
        this.quantidadeImediata = quantidadeImediata;
    }
    public String getQuantidadeRegistrar() {
        return quantidadeRegistrar;
    }
    public void setQuantidadeRegistrar(String quantidadeRegistrar) {
        this.quantidadeRegistrar = quantidadeRegistrar;
    }
    public String getValorUnitario() {
        return valorUnitario;
    }
    public void setValorUnitario(String valorUnitario) {
        this.valorUnitario = valorUnitario;
    }
    public String getValorTotalRegistrar() {
        return valorTotalRegistrar;
    }
    public void setValorTotalRegistrar(String valorTotalRegistrar) {
        this.valorTotalRegistrar = valorTotalRegistrar;
    }
    public String getValorTotalImediato() {
        return valorTotalImediato;
    }
    public void setValorTotalImediato(String valorTotalImediato) {
        this.valorTotalImediato = valorTotalImediato;
    }
}
