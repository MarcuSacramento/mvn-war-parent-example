package br.gov.mj.side.web.view.programa.contrato.formatacaoitens;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensFormatacao;

public class CadastroItemDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Contrato contrato;
	private List<FormatacaoItensContrato> listaItensFormatacao = new ArrayList<FormatacaoItensContrato>();
	private List<ItensFormatacao> itens = new ArrayList<ItensFormatacao>();

	private String codigoIdentificacao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public List<FormatacaoItensContrato> getListaItensFormatacao() {
		return listaItensFormatacao;
	}

	public void setListaItensFormatacao(List<FormatacaoItensContrato> listaItensFormatacao) {
		this.listaItensFormatacao = listaItensFormatacao;
	}

	public List<ItensFormatacao> getItens() {
		return itens;
	}

	public void setItens(List<ItensFormatacao> itens) {
		this.itens = itens;
	}

	public String getCodigoIdentificacao() {
		return codigoIdentificacao;
	}

	public void setCodigoIdentificacao(String codigoIdentificacao) {
		this.codigoIdentificacao = codigoIdentificacao;
	}

}
