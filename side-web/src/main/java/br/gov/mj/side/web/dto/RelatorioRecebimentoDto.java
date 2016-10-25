package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RelatorioRecebimentoDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String codigoPrograma;
	private String unidadeExecutoraPrograma;
	
    private String numeroOF;
    private String numeroNF;
	
	private String nomeBeneficiario;
	private String nomeRepresentante;
	private String telefoneRepresentante;
	private String emailRepresentante;
	private String enderecoBeneficiario;
	
	private List<TermoEntregaitensDto> listaItens = new ArrayList<TermoEntregaitensDto>();
	private List<TermoEntregaitensDto> listaMembros = new ArrayList<TermoEntregaitensDto>();
	
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

	public String getEnderecoBeneficiario() {
		return enderecoBeneficiario;
	}

	public void setEnderecoBeneficiario(String enderecoBeneficiario) {
		this.enderecoBeneficiario = enderecoBeneficiario;
	}
	
	public String getNumeroOF() {
		return numeroOF;
	}

	public void setNumeroOF(String numeroOF) {
		this.numeroOF = numeroOF;
	}

	public String getNumeroNF() {
		return numeroNF;
	}

	public void setNumeroNF(String numeroNF) {
		this.numeroNF = numeroNF;
	}

	public List<TermoEntregaitensDto> getListaItens() {
		return listaItens;
	}

	public void setListaItens(List<TermoEntregaitensDto> listaItens) {
		this.listaItens = listaItens;
	}

	public List<TermoEntregaitensDto> getListaMembros() {
		return listaMembros;
	}

	public void setListaMembros(List<TermoEntregaitensDto> listaMembros) {
		this.listaMembros = listaMembros;
	}
}
