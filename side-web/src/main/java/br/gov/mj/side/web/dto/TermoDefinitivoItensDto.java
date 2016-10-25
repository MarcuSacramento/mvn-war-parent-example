package br.gov.mj.side.web.dto;

import java.io.Serializable;

import javax.persistence.Column;

public class TermoDefinitivoItensDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1823301928117096986L;

	private String idItem;
	private String nomeBem;
	private String nomeMembros;

	private String estadoDeNovo;
	private String funcionandoDeAcordo;
	private String configuradoDeAcordo;

	private String descricaoNaoConfiguradoDeAcordo;
	private String descricaoNaoFuncionandoDeAcordo;

	public String getIdItem() {
		return idItem;
	}

	public void setIdItem(String idItem) {
		this.idItem = idItem;
	}

	public String getNomeBem() {
		return nomeBem;
	}

	public void setNomeBem(String nomeBem) {
		this.nomeBem = nomeBem;
	}

	public String getNomeMembros() {
		return nomeMembros;
	}

	public void setNomeMembros(String nomeMembros) {
		this.nomeMembros = nomeMembros;
	}

	public String getEstadoDeNovo() {
		return estadoDeNovo;
	}

	public void setEstadoDeNovo(String estadoDeNovo) {
		this.estadoDeNovo = estadoDeNovo;
	}

	public String getFuncionandoDeAcordo() {
		return funcionandoDeAcordo;
	}

	public void setFuncionandoDeAcordo(String funcionandoDeAcordo) {
		this.funcionandoDeAcordo = funcionandoDeAcordo;
	}

	public String getConfiguradoDeAcordo() {
		return configuradoDeAcordo;
	}

	public void setConfiguradoDeAcordo(String configuradoDeAcordo) {
		this.configuradoDeAcordo = configuradoDeAcordo;
	}

	public String getDescricaoNaoConfiguradoDeAcordo() {
		return descricaoNaoConfiguradoDeAcordo;
	}

	public void setDescricaoNaoConfiguradoDeAcordo(
			String descricaoNaoConfiguradoDeAcordo) {
		this.descricaoNaoConfiguradoDeAcordo = descricaoNaoConfiguradoDeAcordo;
	}

	public String getDescricaoNaoFuncionandoDeAcordo() {
		return descricaoNaoFuncionandoDeAcordo;
	}

	public void setDescricaoNaoFuncionandoDeAcordo(
			String descricaoNaoFuncionandoDeAcordo) {
		this.descricaoNaoFuncionandoDeAcordo = descricaoNaoFuncionandoDeAcordo;
	}

}
