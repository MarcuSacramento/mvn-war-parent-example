package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumFormaVerificacaoFormatacao implements BaseEnum<String> {

	LOTE("L", "Lote"), UNITARIA("U", "Unitária");

	private String valor;
	private String descricao;

	private EnumFormaVerificacaoFormatacao(String valor, String descricao) {
		this.valor = valor;
		this.descricao = descricao;
	}

	@Override
	public String getValor() {
		return this.valor;
	}

	public String getDescricao() {
		return this.descricao;
	}

}
