package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.TermoDoacao;

public class TermoDoacaoDto implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String codigoPrograma;
    private String unidadeExecutoraPrograma;
    
    private String usuarioLogado;
    
    private EnumOrder order; 
    private String propertyOrder;
    
    private String nomeBeneficiario;
    private Entidade entidade;
    private String nomeRepresentante;
    private String telefoneRepresentante;
    private String emailRepresentante;
    private TermoDoacao termoDoacao;
    private Programa programa;
    private String numeroProcessoSei;
    private String numeroDocumentoSei;
    
    private List<TermoDefinitivoItensDto> listaItens = new ArrayList<TermoDefinitivoItensDto>();
    private List<TermoDefinitivoItensDto> listaMembros = new ArrayList<TermoDefinitivoItensDto>();
    
    public String getCodigoPrograma() {
            return codigoPrograma;
    }

    public void setCodigoPrograma(String codigoPrograma) {
            this.codigoPrograma = codigoPrograma;
    }

    public String getUnidadeExecutoraPrograma() {
            return unidadeExecutoraPrograma;
    }

    public void setUnidadeExecutoraPrograma(String unidadeExecutoraPrograma) {
            this.unidadeExecutoraPrograma = unidadeExecutoraPrograma;
    }

    public String getNomeBeneficiario() {
            return nomeBeneficiario;
    }

    public void setNomeBeneficiario(String nomeBeneficiario) {
            this.nomeBeneficiario = nomeBeneficiario;
    }

    public String getNomeRepresentante() {
            return nomeRepresentante;
    }

    public void setNomeRepresentante(String nomeRepresentante) {
            this.nomeRepresentante = nomeRepresentante;
    }

    public String getTelefoneRepresentante() {
            return telefoneRepresentante;
    }

    public void setTelefoneRepresentante(String telefoneRepresentante) {
            this.telefoneRepresentante = telefoneRepresentante;
    }

    public String getEmailRepresentante() {
            return emailRepresentante;
    }

    public void setEmailRepresentante(String emailRepresentante) {
            this.emailRepresentante = emailRepresentante;
    }

    public List<TermoDefinitivoItensDto> getListaItens() {
            return listaItens;
    }

    public void setListaItens(List<TermoDefinitivoItensDto> listaItens) {
            this.listaItens = listaItens;
    }

    public List<TermoDefinitivoItensDto> getListaMembros() {
            return listaMembros;
    }

    public void setListaMembros(List<TermoDefinitivoItensDto> listaMembros) {
            this.listaMembros = listaMembros;
    }

	public TermoDoacao getTermoDoacao() {
        return termoDoacao;
    }

    public void setTermoDoacao(TermoDoacao termoDoacao) {
        this.termoDoacao = termoDoacao;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public String getNumeroProcessoSei() {
        return numeroProcessoSei;
    }

    public void setNumeroProcessoSei(String numeroProcessoSei) {
        this.numeroProcessoSei = numeroProcessoSei;
    }

    public String getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(String usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public String getNumeroDocumentoSei() {
        return numeroDocumentoSei;
    }

    public void setNumeroDocumentoSei(String numeroDocumentoSei) {
        this.numeroDocumentoSei = numeroDocumentoSei;
    }

    public EnumOrder getOrder() {
        return order;
    }

    public void setOrder(EnumOrder order) {
        this.order = order;
    }

    public String getPropertyOrder() {
        return propertyOrder;
    }

    public void setPropertyOrder(String propertyOrder) {
        this.propertyOrder = propertyOrder;
    }
}